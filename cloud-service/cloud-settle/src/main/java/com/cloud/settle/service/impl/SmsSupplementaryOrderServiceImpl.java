package com.cloud.settle.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.mapper.SmsSupplementaryOrderMapper;
import com.cloud.settle.service.ISmsSupplementaryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 物耗申请单 Service业务层处理
 *
 * @author cs
 * @date 2020-05-26
 */
@Service
public class SmsSupplementaryOrderServiceImpl extends BaseServiceImpl<SmsSupplementaryOrder> implements ISmsSupplementaryOrderService {
    @Autowired
    private SmsSupplementaryOrderMapper smsSupplementaryOrderMapper;

}
