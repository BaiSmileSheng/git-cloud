package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

/**
 * 延期索赔审核工作流
 * @Author Lihongxia
 * @Date 2020-06-02
 */
public interface IActSmsDelaysDeliveryService {

    /**
     * 延期索赔工作流根据业务key获取延期索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    R getBizInfoByTableId(String businessKey);

    /**
     * 供应商申诉延期索赔开启流程
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param ossIds
     * @param sysUser 用户信息
     * @return
     */
    R addSave(Long id,String complaintDescription, String ossIds, SysUser sysUser);

    /**
     * 延期索赔流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    R audit(BizAudit bizAudit, SysUser sysUser);


}
