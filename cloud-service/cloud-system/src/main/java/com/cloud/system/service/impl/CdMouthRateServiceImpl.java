package com.cloud.system.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdMouthRate;
import com.cloud.system.mapper.CdMouthRateMapper;
import com.cloud.system.service.ICdMouthRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 汇率Service业务层处理
 *
 * @author cs
 * @date 2020-05-27
 */
@Service
public class CdMouthRateServiceImpl extends BaseServiceImpl<CdMouthRate> implements ICdMouthRateService {
    @Autowired
    private CdMouthRateMapper cdMouthRateMapper;


}
