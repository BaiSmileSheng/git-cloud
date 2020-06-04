package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

/**
 * 其他索赔审核工作流
 * @Author Lihongxia
 * @Date 2020-06-02
 */
public interface IActSmsClaimOtherService {

    /**
     * 根据业务key获取其他索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 其他索赔信息
     */
    R getBizInfoByTableId(String businessKey);

    /**
     * 供应商发起申诉时 其他索赔信息开启流程
     * @param smsClaimOtherReq 其他索赔信息
     * @param sysUser 当前用户信息
     * @param files  文件
     * @return 成功或失败
     */
    R addSave(String smsClaimOtherReq, MultipartFile[] files, SysUser sysUser);

    /**
     * 其他索赔审批流程
     * @param bizAudit
     * @param sysUser 当前用户信息
     * @return
     */
    R audit(BizAudit bizAudit, SysUser sysUser);


}
