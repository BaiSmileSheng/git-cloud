package com.cloud.system.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdSupplierInfo;
import com.cloud.system.mapper.CdSupplierInfoMapper;
import com.cloud.system.service.ICdSupplierInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 供应商信息 Service业务层处理
 *
 * @author cs
 * @date 2020-05-28
 */
@Service
public class CdSupplierInfoServiceImpl extends BaseServiceImpl<CdSupplierInfo> implements ICdSupplierInfoService {
    @Autowired
    private CdSupplierInfoMapper cdSupplierInfoMapper;


    }
