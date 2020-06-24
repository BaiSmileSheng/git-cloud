package com.cloud.settle.service;

import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsClaimCashDetail;

import java.util.Map;

/**
 * 索赔兑现明细 Service接口
 *
 * @author cs
 * @date 2020-06-05
 */
public interface ISmsClaimCashDetailService extends BaseService<SmsClaimCashDetail> {

    /**
     * 本月兑现扣款
     * @param settleNo
     * @return
     */
    Map<String, SmsClaimCashDetail> selectSumCashGroupByClaimTypeActual(String settleNo);

    /**
     * 历史兑现扣款
     * @param settleNo
     * @return
     */
    Map<String, SmsClaimCashDetail> selectSumCashGroupByClaimTypeHistory(String settleNo);
}
