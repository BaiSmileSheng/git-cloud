package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.mapper.CdFactoryLineInfoMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import com.cloud.system.service.ICdFactoryLineInfoService;
import com.cloud.system.service.SystemFromSap600InterfaceService;
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
    private SystemFromSap600InterfaceService systemFromSap600InterfaceService;
    @Autowired
    private ICdFactoryInfoService cdFactoryInfoService;

    /**
     * 根据供应商编号查询线体
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    @Override
    public R selectLineCodeBySupplierCode(String supplierCode) {
        return R.data(cdFactoryLineInfoMapper.selectLineCodeBySupplierCode(supplierCode));
    }

    /**
     * 根据线体查询信息
     * @param produceLineCode
     * @return 供应商编码
     */
    @Override
    public CdFactoryLineInfo selectInfoByCodeLineCode(String produceLineCode) {
        return cdFactoryLineInfoMapper.selectInfoByCodeLineCode(produceLineCode);
    }
    /**
     * 1、调用SAP系统获取工厂线体关系数据接口
     * 2、保存获取的工厂线体关系数据
     *  2-1、判断数据库中是否存在，数据分类
     *  2-2、执行不同的sql
     * @Description: 获取SAP系统工厂线体关系数据，保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @Override
    public R saveFactoryLineInfo() {
        //1、调用SAP系统获取工厂线体关系数据接口
        R factoryLineSap = systemFromSap600InterfaceService.queryFactoryLineFromSap600();
        if (!factoryLineSap.isSuccess()) {
            log.error("调用SAP系统获取工厂线体关系数据接口失败，原因："+factoryLineSap.get("msg"));
            return R.error("调用SAP系统获取工厂线体关系数据接口失败，原因："+factoryLineSap.get("msg"));
        }
        //2、保存获取的工厂线体关系数据
        List<CdFactoryLineInfo> cdFactoryLineInfos = (List<CdFactoryLineInfo>)factoryLineSap.get("data");
        //新增数据List
        List<CdFactoryLineInfo> insertFactoryLine = new ArrayList<>();
        //更新数据List
        List<CdFactoryLineInfo> updateFactoryLine = new ArrayList<>();
            //2-1、判断数据库中是否存在，数据分类
            if (cdFactoryLineInfos.size() > 0) {
                for (CdFactoryLineInfo cdFactoryLineInfo : cdFactoryLineInfos) {
                    CdFactoryLineInfo factoryLineInfo = new CdFactoryLineInfo();
                    factoryLineInfo.setProductFactoryCode(cdFactoryLineInfo.getProductFactoryCode());
                    factoryLineInfo.setProduceLineCode(cdFactoryLineInfo.getProduceLineCode());
                    factoryLineInfo.setDelFlag("0");
                    //根据工厂、线体查询数据
                    CdFactoryLineInfo checkFactoryLine = cdFactoryLineInfoMapper.selectOne(factoryLineInfo);
                    //根据供应商编码查询描述
                    CdFactoryInfo cdFactoryInfo = cdFactoryInfoService.selectOne(CdFactoryInfo.builder().companyCodeV(cdFactoryLineInfo.getSupplierCode()).build());
                    if (cdFactoryInfo != null) {
                        factoryLineInfo.setSupplierDesc(cdFactoryInfo.getCompanyDesc());//供应商描述
                    }
                    factoryLineInfo.setSupplierCode(cdFactoryLineInfo.getSupplierCode());//供应商编码
                    factoryLineInfo.setAttribute(cdFactoryLineInfo.getAttribute());//属性：自制、工序、OEM
                    factoryLineInfo.setMonitor(cdFactoryLineInfo.getMonitor());//班长
                    factoryLineInfo.setBranchOffice(cdFactoryLineInfo.getBranchOffice());//分公司主管
                    if (checkFactoryLine != null) {
                        factoryLineInfo.setId(checkFactoryLine.getId());
                        factoryLineInfo.setUpdateBy("systemJob");
                        updateFactoryLine.add(factoryLineInfo);
                    }else {
                        factoryLineInfo.setCreateTime(new Date());
                        factoryLineInfo.setCreateBy("systemJob");
                        insertFactoryLine.add(factoryLineInfo);
                    }
                }
                //2-2、执行不同的sql
                cdFactoryLineInfoMapper.insertList(insertFactoryLine);
                cdFactoryLineInfoMapper.updateBatchByPrimaryKeySelective(updateFactoryLine);
            }
        return R.ok();
    }
}
