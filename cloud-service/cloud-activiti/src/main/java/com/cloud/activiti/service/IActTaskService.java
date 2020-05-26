package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.common.core.domain.R;

/**
 * activiti审批流
 *
 * @author cs
 * @date 2020-05-20
 */
public interface IActTaskService {

    /**
     * 审批通用方法  推进流程  设置下一审批人
     * @param bizAudit 审批信息  auditUserId 审批人ID
     * @return 是否成功
     */
    R audit(BizAudit bizAudit,long auditUserId);

    /**
     * 批量审批   与单一审批不同的是  taskIds为数组
     * @param bizAudit 审批信息  auditUserId 审批人ID
     * @return 是否成功
     */
    R auditBatch(BizAudit bizAudit,long auditUserId);
}
