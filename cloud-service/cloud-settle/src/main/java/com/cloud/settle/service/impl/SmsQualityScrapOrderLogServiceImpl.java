package com.cloud.settle.service.impl;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.settle.mapper.SmsQualityScrapOrderLogMapper;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.settle.service.ISmsQualityScrapOrderLogService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 质量部报废申诉Service业务层处理
 *
 * @author ltq
 * @date 2020-12-18
 */
@Service
public class SmsQualityScrapOrderLogServiceImpl extends BaseServiceImpl<SmsQualityScrapOrderLog> implements ISmsQualityScrapOrderLogService {
    @Autowired
    private SmsQualityScrapOrderLogMapper smsQualityScrapOrderLogMapper;


    }
