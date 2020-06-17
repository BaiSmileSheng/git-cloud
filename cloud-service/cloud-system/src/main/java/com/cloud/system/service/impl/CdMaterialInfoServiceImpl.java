package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.utils.StringUtils;
import com.cloud.common.utils.XmlUtil;
import com.cloud.system.config.MdmConnConfig;
import com.cloud.system.service.SystemFromSap601InterfaceService;
import com.cloud.system.webService.material.GeneralMDMDataReleaseBindingStub;
import com.cloud.system.webService.material.Generalmdmdatarelease_client_epLocator;
import com.cloud.system.webService.material.ProcessResponse;
import com.cloud.system.webService.material.RowRisk;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdMaterialInfoMapper;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.common.core.service.impl.BaseServiceImpl;

import javax.xml.rpc.holders.StringHolder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * @Description: 保存MDM接口获取的物料信息数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/5/29
     */
    @Override
    public R saveMaterialInfo() {
        List<RowRisk> list = new ArrayList<>();
        List<CdMaterialInfo> cdMaterialInfosInsert = new ArrayList<>();
        List<CdMaterialInfo> cdMaterialInfosUpdate = new ArrayList<>();
        try {
            //接口获取物料数据
            R r = materialInfoInterface(list, 0, null);
            SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (r.isSuccess()) {
                list = (List<RowRisk>) r.get("list");
                for (RowRisk rowRisk : list) {
                    CdMaterialInfo cdMaterialInfo = new CdMaterialInfo();
                    cdMaterialInfo.setMaterialCode(rowRisk.getMATERIAL_CODE());
                    //查询物料数据
                    CdMaterialInfo materialInfo = cdMaterialInfoMapper.selectOne(cdMaterialInfo);
                    cdMaterialInfo.setMaterialDesc(rowRisk.getMATERIAL_DESCRITION());
                    cdMaterialInfo.setMaterialType(rowRisk.getMATERIAL_TYPE());
                    cdMaterialInfo.setPrimaryUom(rowRisk.getPRIMARY_UOM());
                    cdMaterialInfo.setMtlGroupCode(rowRisk.getMTL_GROUP_CODE());
                    cdMaterialInfo.setPlantCode(rowRisk.getPLANT_CODE());
                    cdMaterialInfo.setPurchaseGroupCode(rowRisk.getPURCHASE_GROUP_CODE());
                    cdMaterialInfo.setRoundingQuantit(new BigDecimal(StringUtils.isNotBlank(rowRisk.getROUNDING_QUANTITY().trim()) ? rowRisk.getROUNDING_QUANTITY().trim() : "0"));
                    cdMaterialInfo.setLastUpdate(sft.parse(rowRisk.getLAST_UPD()));
                    cdMaterialInfo.setCreateTime(new Date());
                    cdMaterialInfo.setCreateBy("systemJob");
                    cdMaterialInfo.setDelFlag("0");
                    //判断物料是否已存在
                    if (materialInfo != null) {
                        cdMaterialInfo.setId(materialInfo.getId());
                        cdMaterialInfo.setUpdateBy("systemJob");
                        cdMaterialInfosUpdate.add(cdMaterialInfo);
                    } else {
                        cdMaterialInfosInsert.add(cdMaterialInfo);
                    }
                }
                if (cdMaterialInfosInsert.size() > 0) {
                    //插入新数据
                    cdMaterialInfoMapper.insertList(cdMaterialInfosInsert);
                } else if (cdMaterialInfosUpdate.size() > 0) {
                    //更新数据
                    cdMaterialInfoMapper.updateBatchByPrimaryKeySelective(cdMaterialInfosUpdate);
                }
            } else {
                log.error(r.get("msg").toString());
                return R.error(r.get("msg").toString());
            }
        } catch (Exception e) {
            log.error("保存物料主数据失败！");
            return R.error("保存物料主数据失败:" + e);
        }
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
            long startTime = System.currentTimeMillis(); // 获取开始时间
            //获取链接
            GeneralMDMDataReleaseBindingStub generalMDMDataReleaseBindingStub =
                    (GeneralMDMDataReleaseBindingStub) new Generalmdmdatarelease_client_epLocator(mdmConnConfig).getGeneralMDMDataRelease_pt();
            //调外部接口方法
            generalMDMDataReleaseBindingStub.process(mdmConnConfig.getSysName(), mdmConnConfig.getMasterType(),
                    mdmConnConfig.getTableName(), startDateString, endDateString, String.valueOf(page), batchId,
                    outPage, outResult, outRetcode, outAllNum, outPageCon, pageAll, outRetmsg, onBatchId);
            long endTime = System.currentTimeMillis(); // 获取结束时间
            log.info("调接口一次往返时间： " + (endTime - startTime) + "ms");
            //判断返回的状态
            if (!"S".equals(outRetcode.value)) {
                log.error("获取物料主数据接口调用失败:" + outRetmsg.value);
                return R.error("获取物料主数据接口调用失败:" + outRetmsg.value);
            }
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
    public int updateBatchByFactoryAndMaterial(List<CdMaterialInfo> list) {
        return cdMaterialInfoMapper.updateBatchByFactoryAndMaterial(list);
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
        List<CdMaterialInfo> list = cdMaterialInfoMapper.select(CdMaterialInfo.builder().delFlag("0").build());
        //2处理UPH接口的入参数据
        //工厂set
        Set<String> factorySet = new HashSet<>();
        //物料set
        Set<String> materialSet = new HashSet<>();
        if (list.size() <= 0) {
            log.error("查询UPH接口入参数据为空！");
            return R.error("查询UPH接口入参数据为空！");
        }
        //遍历获取生产工厂、物料且去重
        for (CdMaterialInfo cdMaterialInfo : list) {
            factorySet.add(cdMaterialInfo.getPlantCode());
            materialSet.add(cdMaterialInfo.getMaterialCode());
        }
        //set ——> List
        List<String> factorys = new ArrayList<>(factorySet);
        List<String> materials = new ArrayList<>(materialSet);
        //3调用SAP系统UPH接口
        R r = systemFromSap601InterfaceService.queryUphFromSap601(factorys, materials);
        if (!r.isSuccess()) {
            log.error("调用SAP系统UPH接口返回数据失败，原因：" + r.get("msg"));
            return R.error("调用SAP系统UPH接口返回数据失败，原因：" + r.get("msg"));
        }
        List<CdMaterialInfo> uphList = (List<CdMaterialInfo>) r.get("data");
        //4执行更新方法
        cdMaterialInfoMapper.updateBatchByFactoryAndMaterial(uphList);
        return R.ok();
    }

    /**
     * 根据物料号集合查询物料信息
     * @param materialCodes
     * @param materialType
     * @return
     */
    @Override
    public R selectInfoByInMaterialCodeAndMaterialType(List<String> materialCodes,String materialType) {
        return R.data(cdMaterialInfoMapper.selectInfoByInMaterialCodeAndMaterialType(materialCodes, materialType));
    }
}
