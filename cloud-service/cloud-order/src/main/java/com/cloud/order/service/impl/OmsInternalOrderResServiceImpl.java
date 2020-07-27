package com.cloud.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.order.domain.entity.OmsInternalOrderRes;
import com.cloud.order.mapper.OmsInternalOrderResMapper;
import com.cloud.order.service.IOmsInternalOrderResService;
import com.cloud.order.service.IOrderFromSap800InterfaceService;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.feign.RemoteBomService;
import com.cloud.system.feign.RemoteFactoryInfoService;
import com.cloud.system.feign.RemoteMaterialService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内单PR/PO原 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-05
 */
@Service
public class OmsInternalOrderResServiceImpl extends BaseServiceImpl<OmsInternalOrderRes> implements IOmsInternalOrderResService {

    private static Logger logger = LoggerFactory.getLogger(OmsInternalOrderResServiceImpl.class);

    @Autowired
    private OmsInternalOrderResMapper omsInternalOrderResMapper;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteBomService remoteBomService;
    @Autowired
    private IOrderFromSap800InterfaceService orderFromSap800InterfaceService;
    @Autowired
    private RemoteMaterialService remoteMaterialService;

    static final String BOM_VERSION_EIGHT = "8";

    static final String BOM_VERSION_NINE = "9";


    @Override
    public R insert800PR(List<OmsInternalOrderRes> list) {


        // 1根据供应商V码去工厂信息表（cd_factory_info）中获取生产工厂编码及名称
        //2 首先通过生产工厂、成品物料号去BOM清单表（cd_bom_info）中获取BOM的版本号，优先8、9版本，有8选8，没8取9，其他取最小版本；
        R rFactory = remoteFactoryInfoService.selectAllByCompanyCodeV(null);
        if (!rFactory.isSuccess()) {
            return R.error("工厂信息为空！！！");
        }
        Map<String, CdFactoryInfo> factoryInfoMap=rFactory.getCollectData(new TypeReference<Map<String, CdFactoryInfo>>() {});
        if (MapUtil.isEmpty(factoryInfoMap)) {
            return R.error("获取工厂编码失败！");
        }

        Date date = DateUtil.date();
        list.forEach(internalOrderRes -> {
            internalOrderRes.setCreateBy("定时任务");
            internalOrderRes.setCreateTime(date);
            String supplieCode = internalOrderRes.getSupplierCode();
            if (factoryInfoMap.get(supplieCode) != null) {
                String productFactoryCode = factoryInfoMap.get(supplieCode).getCompanyCode();
                String productFactoryDesc = factoryInfoMap.get(supplieCode).getCompanyDesc();
                internalOrderRes.setProductFactoryCode(productFactoryCode);
                internalOrderRes.setProductFactoryDesc(productFactoryDesc);
            }
        });
        //获取不重复的物料号和工厂Map
        List<Dict> maps = list.stream().map(s -> new Dict().set("productFactoryCode",s.getProductFactoryCode())
                .set("productMaterialCode",s.getProductMaterialCode())).distinct().collect(Collectors.toList());
        //获取bom版本
        R rBomMap = remoteBomService.selectVersionMap(maps);
        if (!rBomMap.isSuccess()) {
            return R.error("获取bom版本失败！");
        }
        Map<String, Map<String, String>> bomMap=rBomMap.getCollectData(new TypeReference<Map<String, Map<String, String>>>() {});
        list.forEach(internalOrderRes -> {
            String productFactoryCode = internalOrderRes.getProductFactoryCode();
            String key = StrUtil.concat(true, internalOrderRes.getProductMaterialCode(), productFactoryCode);
            //key:成品物料号+生产工厂
            if (bomMap.get(key) != null) {
                //获取BOM版本
                String boms = bomMap.get(key).get("version");//逗号分隔多版本拼接
                List<String> bomList = StrUtil.splitTrim(boms,StrUtil.COMMA);
                if (CollUtil.contains(bomList, BOM_VERSION_EIGHT)) {
                    //有8取8
                    internalOrderRes.setVersion(BOM_VERSION_EIGHT);
                } else if (CollUtil.contains(bomList, BOM_VERSION_NINE)){
                    //有9取9
                    internalOrderRes.setVersion(BOM_VERSION_NINE);
                }else{
                    //取最小的
                    internalOrderRes.setVersion(bomList.stream().min((c,d)->StrUtil.compare(c,d,true)).get());
                }
            }
        });
        int rows=insertList(list);
        return rows > 0 ? R.ok() : R.error();
    }

    /**
     * 根据Marker删除
     * @param marker
     * @return
     */
	@Override
	public int deleteByMarker(String marker){
		 return omsInternalOrderResMapper.deleteByMarker(marker);
	}

    @Override
    @GlobalTransactional
    public R SAP800PRFindInternalOrderRes(Date startDate, Date endDate) {
        //删除原有的PR数据
        deleteByMarker("PR");
        //从SAP800获取PR数据
        R prR = orderFromSap800InterfaceService.queryDemandPRFromSap800(startDate,endDate);
        if (!prR.isSuccess()) {
            throw new BusinessException(prR.getStr("msg"));
        }
        List<OmsInternalOrderRes> list = prR.getCollectData(new TypeReference<List<OmsInternalOrderRes>>() {});
        if (CollUtil.isEmpty(list)) {
            return R.error("未取到PR数据！");
        }
        //插入
        R rInsert = insert800PR(list);
        return rInsert;
    }

    /**
     * 获取PO接口定时任务
     * @return
     */
    @Transactional
    @Override
    public R timeInsertFromSAP() {
        //获取两个月前的时间
        Date startTime = DateUtils.getMonthTime(-2);
        //1.从SAP800 获取数据 po数据
        R resultSAP = orderFromSap800InterfaceService.queryDemandPOFromSap800(startTime,new Date());
        if(!resultSAP.isSuccess()){
            return R.error(resultSAP.get("msg").toString());
        }
        List<OmsInternalOrderRes> omsInternalOrderResList = resultSAP.getCollectData(new TypeReference<List<OmsInternalOrderRes>>() {});
        if(CollectionUtils.isEmpty(omsInternalOrderResList)){
            logger.error("获取PO接口定时任务 没有在SAP到数据");
            throw new BusinessException("获取PO接口定时任务 没有在SAP到数据");
        }
        //2.根据供应商v码获取 工厂编号 cd_factory_info company_code_v -- company_code
        R resultFactory = remoteFactoryInfoService.listAll();
        if(!resultFactory.isSuccess()){
            logger.error("remoteFactoryInfoService.listAll() 异常res:{}", JSONObject.toJSONString(resultFactory));
            throw new BusinessException("根据供应商v码获取 工厂编号 异常");
        }

        List<CdFactoryInfo> cdFactoryInfoList = resultFactory.getCollectData(new TypeReference<List<CdFactoryInfo>>() {});
        Map<String,String> cdFactoryInfoMap = cdFactoryInfoList.stream().collect(Collectors.toMap(CdFactoryInfo ::getCompanyCodeV,
                CdFactoryInfo ::getCompanyCode,(key1,key2) -> key2));



        for(OmsInternalOrderRes omsInternalOrderRes : omsInternalOrderResList){
            String supplierCode = omsInternalOrderRes.getSupplierCode();
            String factoryCode = cdFactoryInfoMap.get(supplierCode);
            if(StringUtils.isBlank(factoryCode)){
                logger.error("根据供应商v码获取 工厂编号 异常 req:{},resAll:{}",supplierCode,JSONObject.toJSON(cdFactoryInfoMap));
                throw new BusinessException("根据供应商v码获取 工厂编号 异常");
            }
            omsInternalOrderRes.setProductFactoryCode(factoryCode);
            R cdMaterialInfoResult = remoteMaterialService.getByMaterialCode(omsInternalOrderRes.getProductMaterialCode(),factoryCode);
            if (!cdMaterialInfoResult.isSuccess()) {
                logger.error(StrUtil.format("查物料信息异常 productMaterialCode:{},factoryCode:{},res:{}", omsInternalOrderRes.getProductMaterialCode(),factoryCode,JSONObject.toJSON(cdMaterialInfoResult)));
                throw new BusinessException("物料信息表未维护物料信息");
            }
            CdMaterialInfo cdMaterialInfo = cdMaterialInfoResult.getData(CdMaterialInfo.class);
            omsInternalOrderRes.setProductMaterialDesc(cdMaterialInfo.getMaterialDesc());

            //获取bom版本
            Map<String, Map<String, String>> bomMap = bomMap(Arrays.asList(omsInternalOrderRes));
            //通过生产工厂、客户编码去BOM清单表（cd_bom_info）中获取BOM的版本号，优先8、9版本，有8选8，没8取9，其他取最小版本
            String keyBom = StrUtil.concat(true, omsInternalOrderRes.getProductMaterialCode(), omsInternalOrderRes.getProductFactoryCode());
            //key:成品物料号+生产工厂
            if (bomMap.get(keyBom) != null) {
                //获取BOM版本
                String boms = bomMap.get(keyBom).get("version");//逗号分隔多版本拼接
                List<String> bomList = StrUtil.splitTrim(boms,StrUtil.COMMA);
                if (CollUtil.contains(bomList, BOM_VERSION_EIGHT)) {
                    //有8取8
                    omsInternalOrderRes.setVersion(BOM_VERSION_EIGHT);
                } else if (CollUtil.contains(bomList, BOM_VERSION_NINE)){
                    //有9取9
                    omsInternalOrderRes.setVersion(BOM_VERSION_NINE);
                }else{
                    //取最小的
                    omsInternalOrderRes.setVersion(bomList.stream().min((c,d)->StrUtil.compare(c,d,true)).get());
                }
            }

        }
        //3.插入数据
        omsInternalOrderResMapper.batchInsertOrUpdate(omsInternalOrderResList);
        return R.ok();
    }

    /**
     * 获取bom信息
     * @param internalOrderResList
     * @return
     */
    private Map<String, Map<String, String>> bomMap(List<OmsInternalOrderRes> internalOrderResList){
        List<Dict> maps = internalOrderResList.stream().map(s -> new Dict().set("productFactoryCode",s.getProductFactoryCode())
                .set("productMaterialCode",s.getProductMaterialCode())).distinct().collect(Collectors.toList());
        //获取bom版本
        R rbomMap = remoteBomService.selectVersionMap(maps);
        if (!rbomMap.isSuccess()) {
            logger.error("获取bom版本失败 req:{},res:{}",maps,JSONObject.toJSON(rbomMap));
            throw new BusinessException("获取bom版本失败！");
        }
        Map<String, Map<String, String>> bomMap =rbomMap.getCollectData(new TypeReference<Map<String, Map<String, String>>>() {});
        return bomMap;
    }
}
