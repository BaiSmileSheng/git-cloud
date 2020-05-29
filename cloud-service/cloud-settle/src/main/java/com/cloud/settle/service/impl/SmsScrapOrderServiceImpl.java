package com.cloud.settle.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.mapper.SmsScrapOrderMapper;
import com.cloud.settle.service.ISmsScrapOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 报废申请 Service业务层处理
 *
 * @author cs
 * @date 2020-05-29
 */
@Service
public class SmsScrapOrderServiceImpl extends BaseServiceImpl<SmsScrapOrder> implements ISmsScrapOrderService {
    @Autowired
    private SmsScrapOrderMapper smsScrapOrderMapper;


    }
