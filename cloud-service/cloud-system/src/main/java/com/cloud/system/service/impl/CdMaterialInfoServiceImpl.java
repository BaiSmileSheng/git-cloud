package com.cloud.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.utils.StringUtils;
import com.cloud.common.utils.XmlUtil;
import com.cloud.system.config.MdmConnConfig;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.enums.GetStockEnum;
import com.cloud.system.enums.PuttingOutEnum;
import com.cloud.system.mapper.CdMaterialInfoMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import com.cloud.system.service.ICdMaterialExtendInfoService;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.system.service.SystemFromSap601InterfaceService;
import com.cloud.system.webService.material.GeneralMDMDataReleaseBindingStub;
import com.cloud.system.webService.material.Generalmdmdatarelease_client_epLocator;
import com.cloud.system.webService.material.ProcessResponse;
import com.cloud.system.webService.material.RowRisk;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.xml.rpc.holders.StringHolder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物料信息 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-01
 */
@Service
@Slf4j
public class CdMaterialInfoServiceImpl extends BaseServiceImpl<CdMaterialInfo> implements ICdMaterialInfoService {
    @Autowired
    private CdMaterialInfoMapper cdMaterialInfoMapper;
    @Autowired
    private MdmConnConfig mdmConnConfig;
    @Autowired
    private SystemFromSap601InterfaceService systemFromSap601InterfaceService;
    @Autowired
    private ICdMaterialExtendInfoService cdMaterialExtendInfoService;
    @Autowired
    private ICdFactoryInfoService cdFactoryInfoService;
    //成品物料的MRP组
    private static final String[] PRODUCT_MATERIAL_MRP = new String[]{"Z001"};
    //删除标识：0可用
    private static final String DELETE_FLAG = "0";

    /**
     * @Description: 保存MDM接口获取的物料信息数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/5/29
     */
    @Override
    public R saveMaterialInfo() {
        log.info("保存MDM接口获取的物料信息数据方法开始======"+DateUtil.now());
        List<RowRisk> list = new ArrayList<>();
        //接口获取物料数据
        R r = materialInfoInterface(list, 0, null);
        log.info("保存MDM接口获取的物料信息数据方法-调用外部接口结束======"+DateUtil.now());
        if (r.isSuccess()) {
            ObjectMapper objectMapper = new ObjectMapper();
            list = objectMapper.convertValue(r.get("list"), new TypeReference<List<RowRisk>>() {
            });
            if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
                log.info("调用MDM接口获取物料主数据条数："+list.size());
                //新增
                List<CdMaterialInfo> cdMaterialInfosInsertOrUpdate = new ArrayList<>();
                //成品物料扩展信息
                List<CdMaterialExtendInfo> cdMaterialExtendInfosInsertOrUpdate = new ArrayList<>();
                list.forEach(rowRisk -> {
                    CdMaterialInfo cdMaterialInfo = new CdMaterialInfo();
                    cdMaterialInfo.setMaterialCode(rowRisk.getMATERIAL_CODE());
                    cdMaterialInfo.setPlantCode(rowRisk.getPLANT_CODE());
                    cdMaterialInfo.setMaterialDesc(rowRisk.getMATERIAL_DESCRITION());
                    cdMaterialInfo.setMaterialType(rowRisk.getMATERIAL_TYPE());
                    cdMaterialInfo.setPrimaryUom(rowRisk.getPRIMARY_UOM());
                    cdMaterialInfo.setMtlGroupCode(rowRisk.getMTL_GROUP_CODE());
                    cdMaterialInfo.setPurchaseGroupCode(rowRisk.getPURCHASE_GROUP_CODE());
                    cdMaterialInfo.setRoundingQuantit(new BigDecimal(StringUtils.isNotBlank(rowRisk.getROUNDING_QUANTITY().trim()) ? rowRisk.getROUNDING_QUANTITY().trim() : "0"));
                    cdMaterialInfo.setLastUpdate(StrUtil.isNotBlank(rowRisk.getLAST_UPD()) ? DateUtil.parse(rowRisk.getLAST_UPD(), "yyyy-MM-dd HH:mm:ss") : new Date());
                    cdMaterialInfo.setMdmCreateTime(StrUtil.isNotBlank(rowRisk.getCREATED()) ? DateUtil.parse(rowRisk.getCREATED(),"yyyy-MM-dd HH:mm:ss") : new Date());
                    cdMaterialInfo.setDelFlag("0");
                    cdMaterialInfo.setCreateBy("systemJob");
                    cdMaterialInfo.setMdmDeleteFlag(rowRisk.getDELETE_FLAG());
                    cdMaterialInfo.setDepartment(rowRisk.getDEPARTMENT());
                    cdMaterialInfo.setMrpGrpCode(rowRisk.getMRP_GRP_CODE());
                    cdMaterialInfosInsertOrUpdate.add(cdMaterialInfo);
                    //TODO 物料主数据增加MRP组字段，可以判断出成品和原材料，现根据该字段区分成品并存入成品扩展信息表中
                    if (ArrayUtil.contains(PRODUCT_MATERIAL_MRP,rowRisk.getMRP_GRP_CODE())
                            && rowRisk.getDELETE_FLAG().equals(DELETE_FLAG)) {
                        CdMaterialExtendInfo cdMaterialExtendInfo = new CdMaterialExtendInfo();
                        cdMaterialExtendInfo.setMaterialCode(rowRisk.getMATERIAL_CODE());
                        cdMaterialExtendInfo.setMaterialDesc(rowRisk.getMATERIAL_DESCRITION());
                        cdMaterialExtendInfo.setEstablishDate(StrUtil.isNotBlank(rowRisk.getCREATED()) ? DateUtil.parse(rowRisk.getCREATED(),"yyyy-MM-dd HH:mm:ss") : null);
                        cdMaterialExtendInfo.setDelFlag("0");
                        cdMaterialExtendInfo.setIsPuttingOut(PuttingOutEnum.IS_PUTTING_OUT_1.getCode());
                        cdMaterialExtendInfo.setIsGetStock(GetStockEnum.IS_GET_STOCK_1.getCode());
                        cdMaterialExtendInfo.setCreateBy("systemJob");
                        cdMaterialExtendInfo.setUpdateBy("systemJob");
                        cdMaterialExtendInfosInsertOrUpdate.add(cdMaterialExtendInfo);
                    }
                });
                cdMaterialInfoMapper.batchInsetOrUpdate(cdMaterialInfosInsertOrUpdate);
                if (cdMaterialExtendInfosInsertOrUpdate.size() > 0) {
                    cdMaterialExtendInfoService.batchMaterialInsertOrUpdate(cdMaterialExtendInfosInsertOrUpdate);
                }
            } else {
                log.info("接口获取物料主数据为空！");
                return R.ok("接口获取物料主数据为空！");
            }
        } else {
            log.error(r.get("msg").toString());
            return R.error(r.get("msg").toString());
        }
        log.info("保存MDM接口获取的物料信息数据方法结束======"+DateUtil.now());
        return R.ok();
    }

    /**
     * @Description: 接口获取MDM物料信息
     * @Param: [list, pageAll, page, batchId]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/5/29
     */
    @Override
    public R materialInfoInterface(List<RowRisk> list, int page, String batchId) {
        page += 1;
        log.info("第"+page+"次调用获取MDM物料信息接口方法开始时间："+DateUtil.now());
        //定义返回对象
        R r = new R();
        StringHolder outPage = new StringHolder();
        StringHolder outResult = new StringHolder();
        StringHolder outRetcode = new StringHolder();
        StringHolder outAllNum = new StringHolder();
        StringHolder outPageCon = new StringHolder();
        StringHolder outRetmsg = new StringHolder();
        StringHolder onBatchId = new StringHolder();
        StringHolder pageAll = new StringHolder();
        SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDateTime = new Date();
        Date startDateTime = new Date();
        try {
            Calendar cl = Calendar.getInstance();
            cl.setTime(startDateTime);
            cl.add(Calendar.DATE, -1);//减一天
            String startDateString = sft.format(cl.getTime());
            String endDateString = sft.format(endDateTime);
            log.info("第"+page+"次调用外部接口开始时间："+DateUtil.now());
            //获取链接
            GeneralMDMDataReleaseBindingStub generalMdmDataReleaseBindingStub =
                    (GeneralMDMDataReleaseBindingStub) new Generalmdmdatarelease_client_epLocator(mdmConnConfig).getGeneralMDMDataRelease_pt();
            //调外部接口方法
            generalMdmDataReleaseBindingStub.process(mdmConnConfig.getSysName(), mdmConnConfig.getMasterType(),
                    mdmConnConfig.getTableName(), startDateString, endDateString, String.valueOf(page), batchId,
                    outPage, outResult, outRetcode, outAllNum, outPageCon, pageAll, outRetmsg, onBatchId);
            log.info("第"+page+"次调用外部接口结束时间："+DateUtil.now());
            //判断返回的状态
            if (!"S".equals(outRetcode.value)) {
                if (!outRetmsg.value.contains("无数据")) {
                    log.error("获取物料主数据接口调用失败:" + outRetmsg.value);
                    return R.error("获取物料主数据接口调用失败:" + outRetmsg.value);
                } else {
                    log.info("获取物料主数据接口执行结束:" + outRetmsg.value);
                    return R.ok("获取物料主数据接口执行结束:" + outRetmsg.value);
                }
            }
            log.info("第"+page+"次解析返回的XML开始时间："+DateUtil.now());
            //处理返回的xml字符串
            String xml1 = outResult.value.substring(0, outResult.value.indexOf("<NAME>"))
                    + outResult.value.substring(outResult.value.indexOf("<ROW>"));
            //xml ——>list<bean>
            Object obj = XmlUtil.xmlToListBean(xml1, "OUTPUT", ProcessResponse.class
                    , new String[]{"ROW"}, new String[]{"ROWSET"}, new Class[]{RowRisk.class});
            //判断数据
            if (obj != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                //object -> processResponse
                ProcessResponse processResponse = objectMapper.convertValue(obj, ProcessResponse.class);
                List<RowRisk> rowRisks = processResponse.getROWSET();
                list.addAll(rowRisks);
            }
            log.info("第"+page+"次解析返回的XML结束时间："+DateUtil.now());
            log.info("第"+page+"次获取MDM物料信息条数："+list.size());
            r.put("list", list);
            r.put("batchId", onBatchId.value);
        } catch (Exception e) {
            log.error("获取物料主数据异常:" + e);
            return R.error("获取物料主数据异常" + e);
        }
        //判断当前页数是否到最大页数
        if (page == Integer.parseInt(pageAll.value)) {
            return r;
        }
        //递归调用
        log.info("第"+page+"次调用获取MDM物料信息接口方法结束时间："+DateUtil.now());
        materialInfoInterface(list, page, onBatchId.value);
        return r;
    }

    /**
     * @Description: 根据工厂、物料批量更新
     * @Param: [list]
     * @return: int
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @Override
    public void updateBatchByFactoryAndMaterial(List<CdMaterialInfo> list) {
        cdMaterialInfoMapper.updateBatchByFactoryAndMaterial(list);
    }

    /**
     * 1获取调SAP系统获取UPH接口的入参信息
     * 2处理UPH接口的入参数据
     * 3调用SAP系统UPH接口
     * 4执行更新方法
     *
     * @Description: 获取UPH数据并更新
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @Override
    public R updateUphBySap() {
        //1获取调SAP系统获取UPH接口的入参信息
        //获取物料
        List<CdMaterialExtendInfo> cdMaterialExtendInfos =
                cdMaterialExtendInfoService.select(CdMaterialExtendInfo.builder().delFlag("0").build());
        //获取工厂
        List<CdFactoryInfo> cdFactoryInfos =
                cdFactoryInfoService.select(CdFactoryInfo.builder().delFlag("0").build());
        //2处理UPH接口的入参数据
        //工厂set
        Set<String> factorySet = new HashSet<>();
        //物料set
        Set<String> materialSet = new HashSet<>();
        if (cdMaterialExtendInfos.size() <= 0) {
            log.error("查询UPH接口物料入参数据为空！");
            return R.error("查询UPH接口物料入参数据为空！");
        }
        if (cdFactoryInfos.size() <= 0) {
            log.error("查询UPH接口工厂入参数据为空！");
            return R.error("查询UPH接口工厂入参数据为空！");
        }
        //遍历获取生产工厂、物料且去重
        cdMaterialExtendInfos.forEach(m ->materialSet.add(m.getMaterialCode()));
        cdFactoryInfos.forEach(f ->factorySet.add(f.getFactoryCode()));
        //set ——> List
        List<String> factorys = new ArrayList<>(factorySet);
        List<String> materials = new ArrayList<>(materialSet);
        //3调用SAP系统UPH接口
        R r = systemFromSap601InterfaceService.queryUphFromSap601(factorys, materials);
        if (!r.isSuccess()) {
            log.error("调用SAP系统UPH接口返回数据失败，原因：" + r.get("msg"));
            return R.error("调用SAP系统UPH接口返回数据失败，原因：" + r.get("msg"));
        }
        List<CdMaterialInfo> uphList =
                r.getCollectData(new TypeReference<List<CdMaterialInfo>>() {
                });
        if (ObjectUtil.isNotEmpty(uphList) && uphList.size() > 0) {
            //4执行更新方法
            cdMaterialInfoMapper.updateBatchByFactoryAndMaterial(uphList);
        }
        return R.ok();
    }

    /**
     * Description:  根据成品专用号、生产工厂、物料类型查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @Override
    public R selectListByMaterialList(List<Dict> list) {
        return R.data(cdMaterialInfoMapper.selectListByMaterialList(list));
    }

    @Override
    public R selectListByMaterialCodeList(List<Dict> list) {
        return R.data(cdMaterialInfoMapper.selectListByMaterialCodeList(list));
    }

    @Override
    public R selectByMaterialCode(String materialCode) {
        List<String> materialCodeList = cdMaterialInfoMapper.selectByMaterialCode(materialCode);
        if(CollectionUtils.isEmpty(materialCodeList)){
            return R.error("没有数据");
        }
        Example example = new Example(CdMaterialInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("materialCode", materialCodeList);
        List<CdMaterialInfo> listSelect = cdMaterialInfoMapper.selectByExample(example);
        Map<String,CdMaterialInfo> map = listSelect.stream().collect(Collectors.toMap(cdMaterialInfo ->cdMaterialInfo.getMaterialCode(),
                cdMaterialInfo ->cdMaterialInfo,(key1,key2) ->key2));
        List<CdMaterialInfo> list = map.values().stream().collect(Collectors.toList());
        return R.data(list);
    }

    /**
     * 根据物料号集合查询物料信息
     *
     * @param materialCodes
     * @param materialType
     * @return
     */
    @Override
    public R selectInfoByInMaterialCodeAndMaterialType(List<String> materialCodes, String materialType) {
        List<CdMaterialInfo> list = cdMaterialInfoMapper.selectInfoByInMaterialCodeAndMaterialType(materialCodes, materialType);
        if (CollUtil.isEmpty(list)) {
            return R.data(null);
        }
        Map<String, List<CdMaterialInfo>> map = list.stream().collect(Collectors.groupingBy(CdMaterialInfo::getMaterialCode));
        return R.data(map);
    }
}
