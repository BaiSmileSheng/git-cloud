package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsSettleInfo;

/**
 * 加工费结算 Service接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface ISmsSettleInfoService extends BaseService<SmsSettleInfo> {

    /**
     * 计算加工费(定时任务调用)
     * @return 成功或失败
     */
    R smsSettleInfoCalculate();

}
