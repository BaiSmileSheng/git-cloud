package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;

/**
 * 物耗申请单 审核
 *
 * @author cloud
 * @date 2020-01-07
 */
public interface IActSmsSupplementaryOrderService {
    /**
     * 开启流程 物耗申请单逻辑
     *
     * @param smsSupplementaryOrder
     * @return R
     */
    R startAct(SmsSupplementaryOrder smsSupplementaryOrder,long userId);


    /**
     * 审批流程 物耗申请单逻辑
     * @param bizAudit
     * @param smsSupplementaryOrder
     * @param userId
     * @return R
     */
    R audit(BizAudit bizAudit,SmsSupplementaryOrder smsSupplementaryOrder,long userId);
}
