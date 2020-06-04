package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.mapper.CdFactoryLineInfoMapper;
import com.cloud.system.service.ICdFactoryLineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 工厂线体关系 Service业务层处理
 *
 * @author cs
 * @date 2020-06-01
 */
@Service
public class CdFactoryLineInfoServiceImpl extends BaseServiceImpl<CdFactoryLineInfo> implements ICdFactoryLineInfoService {
    @Autowired
    private CdFactoryLineInfoMapper cdFactoryLineInfoMapper;

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
}
