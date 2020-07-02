package com.cloud.system.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.enums.AttributeType;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.mapper.CdFactoryLineInfoMapper;
import com.cloud.system.service.ICdFactoryLineInfoService;
import com.cloud.system.service.SystemFromSap601InterfaceService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工厂线体关系 Service业务层处理
 *
 * @author cs
 * @date 2020-06-01
 */
@Service
@Slf4j
public class CdFactoryLineInfoServiceImpl extends BaseServiceImpl<CdFactoryLineInfo> implements ICdFactoryLineInfoService {
    @Autowired
    private CdFactoryLineInfoMapper cdFactoryLineInfoMapper;
    @Autowired
    private SystemFromSap601InterfaceService systemFromSap601InterfaceService;

    /**
     * 根据供应商编号查询线体
     *
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    @Override
    public R selectLineCodeBySupplierCode(String supplierCode) {
        return R.data(cdFactoryLineInfoMapper.selectLineCodeBySupplierCode(supplierCode));
    }

    /**
     * 根据线体查询信息
     *
     * @param produceLineCode
     * @param factoryCode
     * @return 供应商编码
     */
    @Override
    public CdFactoryLineInfo selectInfoByCodeLineCode(String produceLineCode, String factoryCode) {
        return cdFactoryLineInfoMapper.selectInfoByCodeLineCode(produceLineCode, factoryCode);
    }

    /**
     * 1、调用SAP系统获取工厂线体关系数据接口
     * 2、保存获取的工厂线体关系数据
     * 2-1、判断数据库中是否存在，数据分类
     * 2-2、执行不同的sql
     *
     * @Description: 获取SAP系统工厂线体关系数据，保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @Override
    public R saveFactoryLineInfo() {
        //1、调用SAP系统获取工厂线体关系数据接口
        R factoryLineSap = systemFromSap601InterfaceService.queryFactoryLineFromSap601();
        if (!factoryLineSap.isSuccess()) {
            log.error("调用SAP系统获取工厂线体关系数据接口失败，原因：" + factoryLineSap.get("msg"));
            return R.error("调用SAP系统获取工厂线体关系数据接口失败，原因：" + factoryLineSap.get("msg"));
        }
        //新增
        List<CdFactoryLineInfo> insertFactoryLines = new ArrayList<>();
        //更新
        List<CdFactoryLineInfo> updateFactoryLines = new ArrayList<>();
        //2、保存获取的工厂线体关系数据
        List<CdFactoryLineInfo> cdFactoryLineInfos =
                factoryLineSap.getCollectData(new TypeReference<List<CdFactoryLineInfo>>() {});
        if (cdFactoryLineInfos.size() > 0) {
            cdFactoryLineInfos.forEach(factoryLineInfo -> {
                CdFactoryLineInfo cdFactoryLineInfo =
                        cdFactoryLineInfoMapper.selectInfoByCodeLineCode(factoryLineInfo.getProduceLineCode(),factoryLineInfo.getProductFactoryCode());
                if (ObjectUtil.isNotEmpty(cdFactoryLineInfo)) {
                    if (AttributeType.ONE.getInfo().equals(factoryLineInfo.getAttribute())) {
                        cdFactoryLineInfo.setAttribute(AttributeType.ONE.getCode());
                    } else if (AttributeType.TWO.getInfo().equals(factoryLineInfo.getAttribute())){
                        cdFactoryLineInfo.setAttribute(AttributeType.TWO.getCode());
                    } else if (AttributeType.THREE.getInfo().equals(factoryLineInfo.getAttribute())) {
                        cdFactoryLineInfo.setAttribute(AttributeType.THREE.getCode());
                    } else {
                        cdFactoryLineInfo.setAttribute("0");
                    }
                    cdFactoryLineInfo.setBranchOffice(factoryLineInfo.getBranchOffice());
                    cdFactoryLineInfo.setMonitor(factoryLineInfo.getMonitor());
                    cdFactoryLineInfo.setAttribute(factoryLineInfo.getAttribute());
                    cdFactoryLineInfo.setUpdateBy("systemJob");
                    cdFactoryLineInfo.setUpdateTime(new Date());
                    updateFactoryLines.add(cdFactoryLineInfo);
                } else {
                    factoryLineInfo.setDelFlag("0");
                    factoryLineInfo.setCreateTime(new Date());
                    factoryLineInfo.setCreateBy("systemJob");
                    if (AttributeType.ONE.getInfo().equals(factoryLineInfo.getAttribute())) {
                        factoryLineInfo.setAttribute(AttributeType.ONE.getCode());
                    } else if (AttributeType.TWO.getInfo().equals(factoryLineInfo.getAttribute())){
                        factoryLineInfo.setAttribute(AttributeType.TWO.getCode());
                    } else if (AttributeType.THREE.getInfo().equals(factoryLineInfo.getAttribute())) {
                        factoryLineInfo.setAttribute(AttributeType.THREE.getCode());
                    } else {
                        factoryLineInfo.setAttribute("0");
                    }
                    insertFactoryLines.add(factoryLineInfo);
                }

            });
            //批量更新
            cdFactoryLineInfoMapper.updateBatchByPrimaryKeySelective(updateFactoryLines);
            //批量新增
            cdFactoryLineInfoMapper.insertList(insertFactoryLines);
        } else {
            log.error("接口获取工厂线体信息为空！");
            return R.error("接口获取工厂线体信息为空！");
        }
        return R.ok();
    }

    /**
     * Description:  根据List<Map<String,String>>工厂、线体查询线体信息
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @Override
    public R selectListByMapList(List<Dict> list) {
        return R.data(cdFactoryLineInfoMapper.selectListByMapList(list));
    }
}
