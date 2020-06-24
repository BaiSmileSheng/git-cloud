package com.cloud.activiti.service.impl;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizAuditService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * activiti审批流
 *
 * @author cs
 * @date 2020-05-20
 */
@Service
public class ActTaskServiceImpl implements IActTaskService {
    @Autowired
    private TaskService taskService;

    @Autowired
    private IBizAuditService bizAuditService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private IBizBusinessService businessService;

    /**
     * 审批通用方法  推进流程  设置下一审批人
     *
     * @param bizAudit 审批信息  auditUserId 审批人ID
     * @return 是否成功
     */
    @Override
    public R audit(BizAudit bizAudit, long auditUserId) {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("result", bizAudit.getResult());
        // 审批
        taskService.complete(bizAudit.getTaskId(), variables);
        SysUser user = remoteUserService.selectSysUserByUserId(auditUserId);
        bizAudit.setAuditor(user.getUserName() + "-" + user.getLoginName());
        bizAudit.setAuditorId(user.getUserId());
        //增加审批历史
        bizAuditService.insertBizAudit(bizAudit);
        BizBusiness bizBusiness = new BizBusiness().setId(bizAudit.getBusinessKey())
                .setProcInstId(bizAudit.getProcInstId());
        //如果有下级，设置审批人并推进，没有则结束
        businessService.setAuditor(bizBusiness, bizAudit.getResult(), auditUserId);
        return R.ok();
    }

    /**
     * 批量审批   与单一审批不同的是  taskIds为数组
     * @param bizAudit 审批信息  auditUserId 审批人ID
     * @return 是否成功
     */
    @Override
    public R auditBatch(BizAudit bizAudit, long auditUserId) {
        SysUser user = remoteUserService.selectSysUserByUserId(auditUserId);
        for (String taskId : bizAudit.getTaskIds()) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();
            BizBusiness bizBusiness = businessService.selectBizBusinessById(pi.getBusinessKey());
            if (null != bizBusiness) {
                Map<String, Object> variables = Maps.newHashMap();
                variables.put("result", bizAudit.getResult());
                // 审批
                taskService.complete(taskId, variables);
                // 构建插入审批记录
                BizAudit audit = new BizAudit().setTaskId(taskId).setResult(bizAudit.getResult())
                        .setProcName(bizBusiness.getProcName()).setProcDefKey(bizBusiness.getProcDefKey())
                        .setApplyer(bizBusiness.getApplyer()).setAuditor(user.getUserName() + "-" + user.getLoginName())
                        .setAuditorId(user.getUserId());
                bizAuditService.insertBizAudit(audit);
                businessService.setAuditor(bizBusiness, audit.getResult(), auditUserId);
            }
        }
        return R.ok();
    }
}
