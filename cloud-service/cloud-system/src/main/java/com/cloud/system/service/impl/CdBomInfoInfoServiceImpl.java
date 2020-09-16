package com.cloud.system.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.domain.vo.CdBomInfoOtherSysVo;
import com.cloud.system.mapper.CdBomInfoMapper;
import com.cloud.system.service.ICdBomInfoService;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.system.service.ISysInterfaceLogService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * bom清单数据 Service业务层处理
 *
 * @author cs
 * @date 2020-06-01
 */
@Slf4j
@Service
public class CdBomInfoInfoServiceImpl extends BaseServiceImpl<CdBomInfo> implements ICdBomInfoService {
    @Autowired
    private CdBomInfoMapper cdBomInfoMapper;
    @Autowired
    private ISysInterfaceLogService sysInterfaceLogService;
    @Autowired
    private ICdMaterialInfoService cdMaterialInfoService;

    private final static String PBOM_FLAG_1 = "1";//bom删除标记

    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R 单耗
     */
    @Override
    public R checkBomNum(String productMaterialCode, String rawMaterialCode, int applyNum) {
        if (StrUtil.isBlank(productMaterialCode)) {
            return R.error("参数：成品物料号为空！");
        }
        if (StrUtil.isBlank(rawMaterialCode)) {
            return R.error("参数：原材料物料号为空！");
        }
        if (applyNum==0) {
            return R.error("参数：申请量为空！");
        }
        Example example = new Example(CdBomInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productMaterialCode",productMaterialCode);
        criteria.andEqualTo("rawMaterialCode",rawMaterialCode);
        CdBomInfo cdBomInfo = findByExampleOne(example);
        if (cdBomInfo ==null|| cdBomInfo.getBomNum() == null) {
            return R.error("未维护BOM！");
        }
        int bomNum = cdBomInfo.getBomNum().intValue();//单耗
        if(applyNum%bomNum!=0){
            return R.error("申请量必须是单耗的整数倍！");
        }
        return R.data(bomNum);
    }

    /**
     * 1、获取调用SAP系统获取BOM清单接口的入参
     * 2、组织接口入参数据
     * 3、调用获取BOM清单数据接口
     * 4、执行保存BOM清单数据sql
     *
     * @Description: 定时任务获取BOM清单-保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @Override
    public R saveBomInfoBySap() {
        //TODO:
        //1、获取调用SAP系统获取BOM清单接口的入参
        //2、组织接口入参数据
        //3、调用获取BOM清单数据接口
        //4、执行保存BOM清单数据sql
        return null;
    }
    /**
     * 根据物料号工厂分组取bom版本
     * @return
     */
    @Override
    public Map<String,Map<String, String>> selectVersionMap(List<Dict> dicts) {
        return cdBomInfoMapper.selectVersionMap(dicts);
    }

    @Override
    public R deleteAll() {
        cdBomInfoMapper.deleteAll();
        return R.ok();
    }
    /**
     * Description:  根据成品专用号、生产工厂、版本查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @Override
    public R selectBomList(List<Dict> list) {
        return R.data(cdBomInfoMapper.selectBomList(list));
    }

    @Transactional
    @Override
    public R pbomUpdateBom(CdBomInfoOtherSysVo cdBomInfoOtherSysVo) {
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog();
        sysInterfaceLog.setAppId(cdBomInfoOtherSysVo.getAppId());
        sysInterfaceLog.setInterfaceName(cdBomInfoOtherSysVo.getInterfaceName());
        sysInterfaceLog.setContent(JSONObject.toJSONString(cdBomInfoOtherSysVo));
        try{
            Example exampleDelete = new Example(CdBomInfo.class);
            Example.Criteria criteriaDelete = exampleDelete.createCriteria();
            criteriaDelete.andEqualTo("productMaterialCode",cdBomInfoOtherSysVo.getProductMaterialCode());
            criteriaDelete.andEqualTo("productFactoryCode",cdBomInfoOtherSysVo.getProductFactoryCode());
            criteriaDelete.andEqualTo("version",cdBomInfoOtherSysVo.getVersion());
            cdBomInfoMapper.deleteByExample(exampleDelete);
            if(!PBOM_FLAG_1.equals(cdBomInfoOtherSysVo.getPbomFlag())){
                List<CdBomInfo> cdBomInfoList = new ArrayList<>();
                List<CdBomInfo> bomDetailList = cdBomInfoOtherSysVo.getBomDetailList();
                if(CollectionUtils.isEmpty(bomDetailList)){
                    throw new BusinessException("新增或修改时无数据");
                }
                List<Dict> paramsMapList = bomDetailList.stream().map(cdBomInfo ->
                                new Dict().set("productFactoryCode", cdBomInfo.getProductFactoryCode())
                                        .set("productMaterialCode", cdBomInfo.getProductMaterialCode()))
                        .distinct().collect(toList());
                R materialInfoR = cdMaterialInfoService.selectListByMaterialList(paramsMapList);
                List<CdMaterialInfo> materialInfoList = new ArrayList<>();
                Map<String,CdMaterialInfo> materialInfoMap = new HashMap<>();
                if(materialInfoR.isSuccess()){
                    materialInfoList = materialInfoR.getCollectData(new TypeReference<List<CdMaterialInfo>>() {});
                    materialInfoList.forEach(materialInfo ->{
                        String key = materialInfo.getMaterialCode()+materialInfo.getPlantCode();
                        materialInfoMap.put(key,materialInfo);
                    });
                }
                bomDetailList.forEach(cdBomInfo ->{
                    ValidatorUtils.validateEntity(cdBomInfo);
                    cdBomInfo.setDelFlag(DeleteFlagConstants.NO_DELETED);
                    cdBomInfo.setProductMaterialCode(cdBomInfoOtherSysVo.getProductMaterialCode());
                    cdBomInfo.setProductMaterialDesc(cdBomInfoOtherSysVo.getProductMaterialDesc());
                    cdBomInfo.setProductFactoryCode(cdBomInfoOtherSysVo.getProductFactoryCode());
                    String version = cdBomInfoOtherSysVo.getVersion().replaceAll("^(0+)", "");
                    cdBomInfo.setVersion(version);
                    cdBomInfo.setCreateBy(cdBomInfoOtherSysVo.getAppId());
                    cdBomInfo.setCreateTime(new Date());
                    cdBomInfo.setUpdateBy(cdBomInfoOtherSysVo.getAppId());
                    cdBomInfo.setUpdateTime(new Date());

                    String key = cdBomInfoOtherSysVo.getProductMaterialCode() + cdBomInfoOtherSysVo.getProductFactoryCode();
                    CdMaterialInfo cdMaterialInfo = materialInfoMap.get(key);
                    if(null != cdMaterialInfo){
                        cdBomInfo.setPurchaseGroup(cdMaterialInfo.getPurchaseGroupCode());
                    }
                    cdBomInfoList.add(cdBomInfo);
                });
                cdBomInfoMapper.insertList(cdBomInfoList);
            }
            sysInterfaceLog.setResults("success");
        }catch (Exception e){
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            log.error(
                    "实时更新bom异常: {}", w.toString());
            sysInterfaceLog.setResults("实时更新bom异常"+w.toString());
            throw new BusinessException("实时更新bom异常" +e.getMessage());
        }finally {
            sysInterfaceLogService.insertSelectiveNoTransactional(sysInterfaceLog);
        }
        return R.ok();
    }
}
