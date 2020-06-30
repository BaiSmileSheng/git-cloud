package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizAuditService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * activiti审批流
 *
 * @author cs
 * @date 2020-05-20
 */
@Service
@Slf4j
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
    @Autowired
    private RepositoryService repositoryService;

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


    /**
     * Description:  开启审批流
     * Param: [key, orderId, orderCode, userId, title]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    @Override
    public R startActProcess(String key, String orderId, String orderCode, Long userId,String title) {

        R keyMap = getByKey(key);
        if (!keyMap.isSuccess()) {
            log.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        //插入流程物业表  并开启流程
        BizBusiness business =
                initBusiness(processDefinitionAct.getId(),processDefinitionAct.getName(),orderId,orderCode, userId,title);
        businessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        businessService.startProcess(business, variables);
        return R.ok("提交成功！");
    }

    @Override
    public R getByKey(String key) {
        // 使用repositoryService查询单个流程实例
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
        if (BeanUtil.isEmpty(processDefinition)) {
            log.error("根据Key值查询流程实例失败!");
            return R.error("根据Key值查询流程实例失败！");
        }
        ProcessDefinitionAct processDefinitionAct =
                ProcessDefinitionAct.builder()
                        .id(processDefinition.getId())
                        .name(processDefinition.getName())
                        .category(processDefinition.getCategory())
                        .deploymentId(processDefinition.getDeploymentId())
                        .description(processDefinition.getDescription())
                        .diagramResourceName(processDefinition.getDiagramResourceName())
                        .resourceName(processDefinition.getResourceName())
                        .tenantId(processDefinition.getTenantId())
                        .version(processDefinition.getVersion()).build();
        return R.data(processDefinitionAct);
    }

    /**
     * Description:  组织数据
     * Param: [procDefId, procName, orderId, orderCode, userId, title]
     * return: com.cloud.activiti.domain.BizBusiness
     * Author: cs
     * Date: 2020/6/24
     */
    private BizBusiness initBusiness(String procDefId,String procName,String orderId,String orderCode, long userId,String title) {

        BizBusiness business = new BizBusiness();
        business.setOrderNo(orderCode);
        business.setTableId(orderId);
        business.setProcDefId(procDefId);
        business.setTitle(title);
        business.setProcName(procName);
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}