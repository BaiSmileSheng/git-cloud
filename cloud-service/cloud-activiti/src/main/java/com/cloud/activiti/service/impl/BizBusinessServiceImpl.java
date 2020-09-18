/*
 * @(#)BizBusinessServiceImpl.java 2020年1月6日 下午3:38:49
 * Copyright 2020 zmr, Inc. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.mapper.BizBusinessMapper;
import com.cloud.activiti.service.IBizAuditService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.activiti.service.IBizNodeService;
import com.cloud.activiti.vo.HiTaskVo;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Lists;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>File：BizBusinessServiceImpl.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:49</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
@Service
public class BizBusinessServiceImpl implements IBizBusinessService {
    @Autowired
    private BizBusinessMapper businessMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IBizNodeService bizNodeService;
    @Autowired
    private IBizAuditService bizAuditService;
    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 查询流程业务
     *
     * @param id 流程业务ID
     * @return 流程业务
     */
    @Override
    public BizBusiness selectBizBusinessById(String id) {
        return businessMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询流程业务列表
     *
     * @param bizBusiness 流程业务
     * @return 流程业务
     */
    @Override
    public List<BizBusiness> selectBizBusinessList(BizBusiness bizBusiness) {
        return businessMapper.select(bizBusiness);
    }

    /**
     * 新增流程业务
     *
     * @param bizBusiness 流程业务
     * @return 结果
     */
    @Override
    public int insertBizBusiness(BizBusiness bizBusiness) {
        return businessMapper.insertSelective(bizBusiness);
    }

    /**
     * 修改流程业务
     *
     * @param bizBusiness 流程业务
     * @return 结果
     */
    @Override
    public int updateBizBusiness(BizBusiness bizBusiness) {
        return businessMapper.updateByPrimaryKeySelective(bizBusiness);
    }

    /**
     * 删除流程业务对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizBusinessByIds(String ids) {
        return businessMapper.deleteByIds(ids);
    }

    /**
     * 删除流程业务信息
     *
     * @param id 流程业务ID
     * @return 结果
     */
    public int deleteBizBusinessById(Long id) {
        return businessMapper.deleteByPrimaryKey(id);
    }

    /* (non-Javadoc)
     * @see com.cloud.activiti.service.IBizBusinessService#deleteBizBusinessLogic(java.lang.String)
     */
    @Override
    public int deleteBizBusinessLogic(String ids) {
        Example example = new Example(BizBusiness.class);
        example.createCriteria().andIn("id", Lists.newArrayList(ids.split(",")));
        return businessMapper.updateByExampleSelective(new BizBusiness().setDelFlag(true), example);
    }

    /* (non-Javadoc)
     * @see com.cloud.activiti.service.IBizBusinessService#startProcess(com.cloud.activiti.domain.BizBusiness, java.util.Map)
     */
    @Override
    public void startProcess(BizBusiness business, Map<String, Object> variables) {
        // 启动流程用户
        identityService.setAuthenticatedUserId(business.getUserId().toString());
        // 启动流程 需传入业务表id变量
        ProcessInstance pi = runtimeService.startProcessInstanceById(business.getProcDefId(),
                business.getId().toString(), variables);
        // 设置流程实例名称
        runtimeService.setProcessInstanceName(pi.getId(), business.getTitle());
        BizBusiness bizBusiness = new BizBusiness().setId(business.getId()).setProcInstId(pi.getId())
                .setProcDefKey(pi.getProcessDefinitionKey());
        // 假如开始就没有任务，那就认为是中止的流程，通常是不存在的
        setAuditor(bizBusiness, ActivitiConstant.RESULT_SUSPEND, business.getUserId());
    }

    /**
     * start 启动流程 动态赋值下一级审批人
     *
     * @param business  业务对象，必须包含id,title,userId,procDefId属性
     * @param variables 启动流程需要的变量
     * @author zmr
     */
    @Override
    public void startProcess(BizBusiness business, Map<String, Object> variables,Set<String> userIds) {
        // 启动流程用户
        identityService.setAuthenticatedUserId(business.getUserId().toString());
        // 启动流程 需传入业务表id变量
        ProcessInstance pi = runtimeService.startProcessInstanceById(business.getProcDefId(),
                business.getId().toString(), variables);
        // 设置流程实例名称
        runtimeService.setProcessInstanceName(pi.getId(), business.getTitle());
        BizBusiness bizBusiness = new BizBusiness().setId(business.getId()).setProcInstId(pi.getId())
                .setProcDefKey(pi.getProcessDefinitionKey())
                .setProcName(business.getProcName())
                .setApplyer(business.getApplyer());
        // 假如开始就没有任务，那就认为是中止的流程，通常是不存在的
        if (CollectionUtil.isEmpty(userIds)) {
            setAuditor(bizBusiness, ActivitiConstant.RESULT_SUSPEND, business.getUserId());
        }else{
            setAuditorCandidateUser(bizBusiness, ActivitiConstant.RESULT_SUSPEND, userIds);
        }
    }

    /**
     * start 启动流程（会签）
     *
     * @param business  业务对象，必须包含id,title,userId,procDefId属性
     * @param variables 启动流程需要的变量
     * @author cs
     */
    @Override
    public void startProcessForHuiQian(BizBusiness business, Map<String, Object> variables) {
        if (ObjectUtil.isEmpty(variables.get("signList"))) {
            throw new BusinessException("开启会签传入审批人参数为空！");
        }
        // 启动流程用户
        identityService.setAuthenticatedUserId(business.getUserId().toString());
        // 启动流程 需传入业务表id变量
        ProcessInstance pi = runtimeService.startProcessInstanceById(business.getProcDefId(),
                business.getId().toString(), variables);
        business.setProcDefKey(pi.getProcessDefinitionKey());
        business.setProcInstId(pi.getProcessInstanceId());
        business.setProcName(business.getProcName());
        business.setApplyer(business.getApplyer());
        // 设置流程实例名称
        runtimeService.setProcessInstanceName(pi.getId(), business.getTitle());

        //下一步生成会签审批
        List<Task> tasks =  taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        if (CollectionUtil.isNotEmpty(tasks)) {
            business.setCurrentTask(tasks.get(0).getName());
        }else{
            // 任务结束
            business.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_FINISH)
                    .setResult(ActivitiConstant.RESULT_SUSPEND);
        }
        updateBizBusiness(business);

        Set<String> userIds = (Set<String>) variables.get("signList");
        // 添加审核候选人展示
        int index = 0;
        for (String auditor : userIds) {
            Task task = tasks.get(index);
            SysUser user = remoteUserService.selectSysUserByUserId(Long.valueOf(auditor));
            BizAudit bizAudit = new BizAudit();
            bizAudit.setTaskId(task.getId());
            bizAudit.setProcDefKey(business.getProcDefKey());
            bizAudit.setProcName(business.getProcName());
            bizAudit.setApplyer(business.getApplyer());
            bizAudit.setAuditor(user.getUserName());
            bizAudit.setAuditorId(user.getUserId());
            bizAudit.setCreateTime(new Date());
            bizAudit.setResult(1);
            bizAuditService.insertBizAudit(bizAudit);
            index++;
        }

    }

    /**
     * 动态赋值下一级审批人
     * @param business
     * @param result
     * @param userIds
     * @return
     */
    @Override
    public int setAuditorCandidateUser(BizBusiness business, int result, Set<String> userIds) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(business.getProcInstId()).list();
        if (null != tasks && tasks.size() > 0) {
            Task task = tasks.get(0);
            if (null != userIds && userIds.size() > 0) {
                List<String> auditorNames = new ArrayList<>();
                // 添加审核候选人
                for (String auditor : userIds) {
                    SysUser user = remoteUserService.selectSysUserByUserId(Long.valueOf(auditor));
                    auditorNames.add(user.getUserName());
                    taskService.addCandidateUser(task.getId(), auditor);
                }
                BizAudit bizAudit = new BizAudit();
                bizAudit.setTaskId(task.getId());
                bizAudit.setProcDefKey(business.getProcDefKey());
                bizAudit.setProcName(business.getProcName());
                bizAudit.setApplyer(business.getApplyer());
                bizAudit.setAuditor(CollectionUtil.join(auditorNames, StrUtil.COMMA));
                bizAudit.setAuditorId(0L);
                bizAudit.setCreateTime(new Date());
                bizAudit.setResult(1);
                bizAuditService.insertBizAudit(bizAudit);

                business.setCurrentTask(task.getName());
            } else {
                runtimeService.deleteProcessInstance(task.getProcessInstanceId(),
                        ActivitiConstant.SUSPEND_PRE + "no auditor");
                business.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_SUSPEND)
                        .setResult(ActivitiConstant.RESULT_SUSPEND);
            }
        } else {
            // 任务结束
            business.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_FINISH)
                    .setResult(result);
        }
        return updateBizBusiness(business);
    }

    @Override
    public int setAuditor(BizBusiness business, int result, long currentUserId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(business.getProcInstId()).list();
        if (null != tasks && tasks.size() > 0) {
            Task task = tasks.get(0);
            Set<String> auditors = bizNodeService.getAuditors(task.getTaskDefinitionKey(), currentUserId);
            if (null != auditors && auditors.size() > 0) {
                // 添加审核候选人
                for (String auditor : auditors) {
                    taskService.addCandidateUser(task.getId(), auditor);
                }

                List<String> auditorNames = new ArrayList<>();
                // 添加审核候选人
                for (String auditor : auditors) {
                    SysUser user = remoteUserService.selectSysUserByUserId(Long.valueOf(auditor));
                    auditorNames.add(user.getUserName());
                    taskService.addCandidateUser(task.getId(), auditor);
                }
                BizAudit bizAudit = new BizAudit();
                bizAudit.setTaskId(task.getId());
                bizAudit.setProcDefKey(business.getProcDefKey());
                bizAudit.setProcName(business.getProcName());
                bizAudit.setApplyer(business.getApplyer());
                bizAudit.setAuditor(CollectionUtil.join(auditorNames, StrUtil.COMMA));
                bizAudit.setAuditorId(0L);
                bizAudit.setCreateTime(new Date());
                bizAudit.setResult(1);
                bizAuditService.insertBizAudit(bizAudit);

                business.setCurrentTask(task.getName());
            } else {
                runtimeService.deleteProcessInstance(task.getProcessInstanceId(),
                        ActivitiConstant.SUSPEND_PRE + "no auditor");
                business.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_SUSPEND)
                        .setResult(ActivitiConstant.RESULT_SUSPEND);
            }
        } else {
            //对审批节点，判断是否全部通过，有一个节点驳回，则整体都驳回
            HiTaskVo hiTaskVo = new HiTaskVo();
            hiTaskVo.setProcInstId(business.getProcInstId());
            List<HiTaskVo> hiTaskVos = bizAuditService.getHistoryTaskList(hiTaskVo);
            hiTaskVos = hiTaskVos.stream()
                    .filter(t -> t.getResult().equals(ActivitiConstant.RESULT_FAIL)).collect(Collectors.toList());
            if (hiTaskVos.size() > 0) {
                result = ActivitiConstant.RESULT_FAIL;
            }
            // 任务结束
            business.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_FINISH)
                    .setResult(result);
        }
        return updateBizBusiness(business);
    }

    @Override
    public String selectByKeyAndTable(String procDefKey, String tableId) {
        Example example = new Example(BizBusiness.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tableId",tableId);
        criteria.andEqualTo("procDefKey",procDefKey);
        List<BizBusiness> bizBusinessList = businessMapper.selectByExample(example);
        Collections.sort(bizBusinessList, Comparator.comparing(BizBusiness::getId).reversed());
        BizBusiness bizBusiness = bizBusinessList.get(0);
        return bizBusiness.getProcInstId();
    }
    /**
     * Description:  根据Example查询
     * Param: [example]
     * return: java.util.List<com.cloud.activiti.domain.BizBusiness>
     * Author: ltq
     * Date: 2020/8/12
     */
    @Override
    public List<BizBusiness> selectByExample(Example example) {
        return businessMapper.selectByExample(example);
    }
}
