package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsScrapOrder;

/**
 * 报废申请单 审核
 *
 * @author cloud
 * @date 2020-01-07
 */
public interface IActSmsScrapOrderService {
    /**
     * 开启流程 报废申请单逻辑
     *
     * @param smsScrapOrder
     * @return R
     */
    R startAct(SmsScrapOrder smsScrapOrder,long userId);


    /**
     * 审批流程 报废申请单逻辑
     * @param bizAudit
     * @param smsScrapOrder
     * @param userId
     * @return R
     */
    R audit(BizAudit bizAudit,SmsScrapOrder smsScrapOrder,long userId);
}
