package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

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
    R startAct(SmsSupplementaryOrder smsSupplementaryOrder, SysUser sysUser, String procDefId, String procName);

    /**
     * 开启流程 物耗申请单逻辑  新增、编辑提交时开启(多条)
     *
     * @param smsSupplementaryOrders
     * @return R
     */
    R startActList(List<SmsSupplementaryOrder> smsSupplementaryOrders, SysUser sysUser);


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
