package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.system.domain.entity.SysUser;

/**
 * 质量索赔审核工作流
 * @Author Lihongxia
 * @Date 2020-06-02
 */
public interface IActSmsQualityOrderService {

    /**
     * 根据业务key获取质量索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    R getBizInfoByTableId(String businessKey);

    /**
     * 质量索赔信息开启流程
     * @param smsQualityOrder 质量索赔信息
     * @param sysUser 当前用户信息
     * @return 成功或失败
     */
    R addSave(SmsQualityOrder smsQualityOrder,SysUser sysUser);

    /**
     * 质量索赔审批流程
     * @param bizAudit
     * @param sysUser 当前用户信息
     * @return
     */
    R audit(BizAudit bizAudit, SysUser sysUser);


}
