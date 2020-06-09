package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsMouthSettle;

/**
 * 月度结算信息 Service接口
 *
 * @author cs
 * @date 2020-06-04
 */
public interface ISmsMouthSettleService extends BaseService<SmsMouthSettle>{

    /**
     * 月度结算定时任务
     * @return
     */
    R countMonthSettle();

    /**
     * 内控确认和小微主确认
     * @param id
     * @param settleStatus
     * @return
     */
    R confirm(Long id,String settleStatus);
}
