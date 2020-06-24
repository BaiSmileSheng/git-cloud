package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

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
     * 供应商申诉时质量索赔开启流程
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param files
     * @return 成功或失败
     */
    R addSave(Long id,String complaintDescription,MultipartFile[] files, SysUser sysUser);

    /**
     * 质量索赔审批流程
     * @param bizAudit
     * @param sysUser 当前用户信息
     * @return
     */
    R audit(BizAudit bizAudit, SysUser sysUser);


}
