package com.cloud.settle.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsClaimCashDetail;
import com.cloud.settle.mapper.SmsClaimCashDetailMapper;
import com.cloud.settle.service.ISmsClaimCashDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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


    /**
     * 本月兑现扣款
     * @param settleNo
     * @return
     */
    @Override
    public Map<String, SmsClaimCashDetail> selectSumCashGroupByClaimTypeActual(String settleNo) {
        return smsClaimCashDetailMapper.selectSumCashGroupByClaimTypeActual(settleNo);
    }

    /**
     * 历史兑现扣款
     * @param settleNo
     * @return
     */
    @Override
    public Map<String, SmsClaimCashDetail> selectSumCashGroupByClaimTypeHistory(String settleNo) {
        return smsClaimCashDetailMapper.selectSumCashGroupByClaimTypeHistory(settleNo);
    }
}
