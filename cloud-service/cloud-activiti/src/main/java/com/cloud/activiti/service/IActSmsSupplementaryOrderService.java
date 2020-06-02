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
     * 开启流程 物耗申请单逻辑  新增、编辑提交时开启
     *
     * @param smsSupplementaryOrder
     * @return R
     */
    R startAct(SmsSupplementaryOrder smsSupplementaryOrder,long userId);

    /**
     * 开启流程 物耗申请单逻辑  列表提交时开启
     *
     * @param smsSupplementaryOrder
     * @return R
     */
    R startActOnlyForList(SmsSupplementaryOrder smsSupplementaryOrder,long userId);


    /**
     * 审批流程 物耗申请单逻辑
     * @param bizAudit
     * @param userId
     * @return R
     */
    R audit(BizAudit bizAudit,long userId);

    /**
     * 根据业务key获取数据
     * @param businessKey
     * @return smsSupplementaryOrder
     * @author cs
     */
    R getBizInfoByTableId(String businessKey);
}
