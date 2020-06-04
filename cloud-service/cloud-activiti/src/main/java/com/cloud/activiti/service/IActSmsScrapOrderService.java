package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.system.domain.entity.SysUser;

/**
 * 报废申请单 审核
 *
 * @author cloud
 * @date 2020-01-07
 */
public interface IActSmsScrapOrderService {
    /**
     * 开启流程 报废申请单逻辑(编辑、新增提交)
     *
     * @param smsScrapOrder
     * @return R
     */
    R startAct(SmsScrapOrder smsScrapOrder, SysUser sysUser);

    /**
     * 开启流程 报废申请单逻辑(列表提交)
     *
     * @param smsScrapOrder
     * @return R
     */
    R startActOnlyForList(SmsScrapOrder smsScrapOrder,long userId);

    /**
     * 审批流程 报废申请单逻辑
     * @param bizAudit
     * @param userId
     * @return R
     */
    R audit(BizAudit bizAudit,long userId);


    R getBizInfoByTableId(String businessKey);
}
