package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.common.core.domain.R;

public interface IActSmsQualityScrapOrderService {
    /**
     * 开启流程 原材料报废申请单逻辑(编辑、新增提交)
     *
     * @param actBusinessVo
     * @return R
     */
    R startAct(ActBusinessVo actBusinessVo);

    R getBizInfoByTableId(String businessKey);

    /**
     * 审批流程 原材料报废申请单逻辑
     * @param bizAudit
     * @param userId
     * @return R
     */
    R audit(BizAudit bizAudit, long userId);

    /**
     * 审批流程 原材料报废申请单逻辑
     * @param bizAudit
     * @param userId
     * @return R
     */
    R auditLogic(BizAudit bizAudit,long userId);
}
