package com.cloud.settle.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.mapper.SmsSettleInfoMapper;
import com.cloud.settle.service.ISmsSettleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 加工费结算 Service业务层处理
 *
 * @author cs
 * @date 2020-05-26
 */
@Service
public class SmsSettleInfoServiceImpl extends BaseServiceImpl<SmsSettleInfo> implements ISmsSettleInfoService {
    @Autowired
    private SmsSettleInfoMapper smsSettleInfoMapper;


}
