package com.cloud.settle.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsClaimCashDetail;
import com.cloud.settle.mapper.SmsClaimCashDetailMapper;
import com.cloud.settle.service.ISmsClaimCashDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 索赔兑现明细 Service业务层处理
 *
 * @author cs
 * @date 2020-06-05
 */
@Service
public class SmsClaimCashDetailServiceImpl extends BaseServiceImpl<SmsClaimCashDetail> implements ISmsClaimCashDetailService {
    @Autowired
    private SmsClaimCashDetailMapper smsClaimCashDetailMapper;


    }
