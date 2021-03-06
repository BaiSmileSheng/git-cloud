package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.mapper.ActRuTaskMapper;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizAuditService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.activiti.vo.HiTaskVo;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HistoryService historyService;
    @Autowired
    private ActRuTaskMapper actRuTaskMapper;

    @Autowired
    private IActTaskService actTaskService;
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
        BizAudit bizAuditUpdate = new BizAudit();
        bizAuditUpdate.setTaskId(bizAudit.getTaskId());
        List<BizAudit> bizAudits = bizAuditService.selectBizAuditList(bizAuditUpdate);
        if (CollectionUtil.isNotEmpty(bizAudits)) {
            bizAuditUpdate = bizAudits.get(0);
            bizAuditUpdate.setAuditor(user.getUserName());
            bizAuditUpdate.setAuditorId(user.getUserId());
            bizAuditUpdate.setResult(bizAudit.getResult());
            bizAuditUpdate.setComment(bizAudit.getComment());
            //更新审批历史
            bizAuditService.updateBizAudit(bizAuditUpdate);
        }
        BizBusiness bizBusiness = new BizBusiness().setId(bizAudit.getBusinessKey())
                .setProcInstId(bizAudit.getProcInstId());
        //如果有下级，设置审批人并推进，没有则结束
        businessService.setAuditor(bizBusiness, bizAudit.getResult(), auditUserId);
        return R.ok();
    }

    @Override
    public R auditCandidateUser(BizAudit bizAudit, long auditUserId,Set<String> userIds) {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("result", bizAudit.getResult());
        // 审批
        taskService.complete(bizAudit.getTaskId(), variables);
        SysUser user = remoteUserService.selectSysUserByUserId(auditUserId);
        BizAudit bizAuditUpdate = new BizAudit();
        bizAuditUpdate.setTaskId(bizAudit.getTaskId());
        List<BizAudit> bizAudits = bizAuditService.selectBizAuditList(bizAuditUpdate);
        if (CollectionUtil.isNotEmpty(bizAudits)) {
            bizAuditUpdate = bizAudits.get(0);
            bizAuditUpdate.setAuditor(user.getUserName());
            bizAuditUpdate.setAuditorId(user.getUserId());
            bizAuditUpdate.setResult(bizAudit.getResult());
            bizAuditUpdate.setComment(bizAudit.getComment());
            //更新审批历史
            bizAuditService.updateBizAudit(bizAuditUpdate);
        }
        BizBusiness bizBusiness = new BizBusiness().setId(bizAudit.getBusinessKey())
                .setProcInstId(bizAudit.getProcInstId())
                .setProcDefKey(bizAudit.getProcDefKey())
                .setProcName(bizAudit.getProcName())
                .setApplyer(bizAudit.getApplyer());
        //如果有下级，设置审批人并推进，没有则结束
        businessService.setAuditorCandidateUser(bizBusiness, ActivitiConstant.RESULT_SUSPEND, userIds);
        return R.ok();
    }

    /**
     *
     * @param 批量审批用
     * @param auditUserId
     * @param userIds
     * @return
     */
    @Override
    public R auditCandidateUserMul(BizAudit bizAudit, long auditUserId,Set<String> userIds) {
//        Map<String, Object> variables = Maps.newHashMap();
//        variables.put("result", bizAudit.getResult());
//        // 审批
//        taskService.complete(bizAudit.getTaskId(), variables);
        SysUser user = remoteUserService.selectSysUserByUserId(auditUserId);
        BizAudit bizAuditUpdate = new BizAudit();
        bizAuditUpdate.setTaskId(bizAudit.getTaskId());
        List<BizAudit> bizAudits = bizAuditService.selectBizAuditList(bizAuditUpdate);
        if (CollectionUtil.isNotEmpty(bizAudits)) {
            bizAuditUpdate = bizAudits.get(0);
            bizAuditUpdate.setAuditor(user.getUserName());
            bizAuditUpdate.setAuditorId(user.getUserId());
            bizAuditUpdate.setResult(bizAudit.getResult());
            bizAuditUpdate.setComment(bizAudit.getComment());
            //更新审批历史
            bizAuditService.updateBizAudit(bizAuditUpdate);
        }
        BizBusiness bizBusiness = new BizBusiness().setId(bizAudit.getBusinessKey())
                .setProcInstId(bizAudit.getProcInstId())
                .setProcDefKey(bizAudit.getProcDefKey())
                .setProcName(bizAudit.getProcName())
                .setApplyer(bizAudit.getApplyer());
        //如果有下级，设置审批人并推进，没有则结束
        businessService.setAuditorCandidateUser(bizBusiness, ActivitiConstant.RESULT_SUSPEND, userIds);
        return R.ok();
    }

    /**
     * 开启会签审批及会签审批人审批方法
     * signers不为null则生成会签审批，如果为null则为会签审批
     * @param bizAudit 审批信息  auditUserId 审批人ID signers 下级审批人ID集合
     * @return 是否成功
     */
    @Override
    public R audit(BizAudit bizAudit, long auditUserId, Set<String> signers) {
        SysUser user = remoteUserService.selectSysUserByUserId(auditUserId);
        BizAudit bizAuditUpdate = new BizAudit();
        bizAuditUpdate.setTaskId(bizAudit.getTaskId());
        List<BizAudit> bizAudits = bizAuditService.selectBizAuditList(bizAuditUpdate).stream().filter(audit -> audit.getAuditorId().equals(auditUserId)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(bizAudits)) {
            bizAuditUpdate = bizAudits.get(0);
            bizAuditUpdate.setAuditor(user.getUserName());
            bizAuditUpdate.setAuditorId(user.getUserId());
            bizAuditUpdate.setResult(bizAudit.getResult());
            bizAuditUpdate.setComment(bizAudit.getComment());
            //更新审批历史
            bizAuditService.updateBizAudit(bizAuditUpdate);
        }

        // 审批
        Map<String, Object> variables = Maps.newHashMap();
        //如果审批驳回，则pass为false 将值传到监听类中
        variables.put("pass", bizAudit.getResult()==2?true:false);
        variables.put("businessKey", bizAudit.getBusinessKey());
        if (CollectionUtil.isNotEmpty(signers)) {
            //给会签节点赋审批人
            variables.put("signList", signers);
        }
        //当会签审批时，会走监听
        taskService.complete(bizAudit.getTaskId(), variables);
        //执行实例
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(bizAudit.getProcInstId()).singleResult();
        BizBusiness bizBusiness = new BizBusiness().setId(bizAudit.getBusinessKey())
                .setProcInstId(bizAudit.getProcInstId());
        if (execution != null) {
            //当前实例的执行到哪个节点
            String activitiId = execution.getActivityId();
            if (activitiId!=null) {
                //会签结束
                //如果有下级，设置审批人并推进，没有则结束
                businessService.setAuditor(bizBusiness, bizAudit.getResult(), auditUserId);
            }
        }else {
            HiTaskVo hiTaskVo = new HiTaskVo();
            hiTaskVo.setProcInstId(bizAudit.getProcInstId());
            List<HiTaskVo> hiTaskVos = bizAuditService.getHistoryTaskList(hiTaskVo);
            int count = hiTaskVos.stream()
                    .filter(o -> ActivitiConstant.RESULT_FAIL.equals(o.getResult())).collect(Collectors.toList()).size();
            if (count > 0) {
                bizBusiness.setResult(ActivitiConstant.RESULT_FAIL);
            } else {
                bizBusiness.setResult(bizAudit.getResult());
            }
            bizBusiness.setCurrentTask(ActivitiConstant.END_TASK_NAME);
            bizBusiness.setStatus(ActivitiConstant.STATUS_FINISH);
            businessService.updateBizBusiness(bizBusiness);
        }

        //如果signers不为null，生成会签流程
        if (CollectionUtil.isNotEmpty(signers)) {
            //下一步生成会签审批
            List<Task> tasks =  taskService.createTaskQuery().processInstanceId(bizAudit.getProcInstId()).list();
            if (CollectionUtil.isNotEmpty(tasks)) {
                bizBusiness.setCurrentTask(tasks.get(0).getName());
            }else{
                //如果下一步没有审批节点，则任务结束(一般不会发生)
                bizBusiness.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_FINISH)
                        .setResult(bizAudit.getResult());
            }
            businessService.updateBizBusiness(bizBusiness);
        }
        return R.ok();
    }

    /**
     * 批量审批   与单一审批不同的是  taskIds为数组
     * @param bizAudit 审批信息  auditUserId 审批人ID
     * @return 是否成功
     */
    @Override
    @GlobalTransactional
    public R auditBatch(BizAudit bizAudit, long auditUserId) {
//        SysUser user = remoteUserService.selectSysUserByUserId(auditUserId);
        for (String taskId : bizAudit.getTaskIds()) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();
            BizBusiness bizBusiness = businessService.selectBizBusinessById(pi.getBusinessKey());
            if (null != bizBusiness) {
                Map<String, Object> variables = Maps.newHashMap();
                variables.put("result", bizAudit.getResult());
                variables.put("taskIdVar", taskId);
                variables.put("bizBusinessId", bizBusiness.getId());
                // 审批  触发监听
                taskService.complete(taskId, variables);
                bizAudit = redisUtils.get(StrUtil.format("bizAudit{}{}", bizBusiness.getId(), taskId),BizAudit.class);
                Set<String> userIds = redisUtils.get(StrUtil.format("userIds{}{}", bizBusiness.getId(), taskId), Set.class);
                Long userId = redisUtils.get(StrUtil.format("userId{}{}", bizBusiness.getId(), taskId),Long.class);
                actTaskService.auditCandidateUserMul(bizAudit, userId,userIds);
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
            throw new BusinessException("根据Key值查询流程实例失败！");
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
     * Description:  根据业务订单号删除审批流程
     * Param: [orderCodeList]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/8/12
     */
    @Override
    @GlobalTransactional
    public R deleteByOrderCode(Map<String,Object> map) {
        log.info("删除审批流程方法-开始执行！");
        ObjectMapper objectMapper = new ObjectMapper();
        String userId = objectMapper.convertValue(map.get("userName"),String.class);
        List<String> orderCodeList =
                objectMapper.convertValue(map.get("orderCodeList"), new TypeReference<List<String>>() {});
        if (ObjectUtil.isEmpty(orderCodeList) || orderCodeList.size() <= 0) {
            log.info("执行删除审批流程方法，传入的业务订单号为空！");
            throw new BusinessException("执行删除审批流程方法，传入的业务订单号为空！");
        }
        Example example = new Example(BizBusiness.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("orderNo",orderCodeList);
        List<BizBusiness> businesses = businessService.selectByExample(example);
        if (ObjectUtil.isEmpty(businesses) || businesses.size() <= 0) {
            log.info("删除审批流程方法-根据业务订单号查询审批流程为空！");
            return R.ok();
        }
        String ids = businesses.stream().map(b -> b.getId().toString()).collect(Collectors.joining(","));
        List<BizBusiness> businessesDealing = businesses.stream()
                .filter(o -> StrUtil.isNotBlank(o.getProcInstId())
                        && ActivitiConstant.RESULT_DEALING.equals(o.getResult())
                        && CollectionUtil.isNotEmpty(actRuTaskMapper.selectByProcInstId(Arrays.asList(o.getProcInstId())))).collect(Collectors.toList());
        List<BizBusiness> businessesPass = businesses.stream()
                .filter(o -> StrUtil.isNotBlank(o.getProcInstId())
                        && !ActivitiConstant.RESULT_DEALING.equals(o.getResult())
                        && !CollectionUtil.isNotEmpty(actRuTaskMapper.selectByProcInstId(Arrays.asList(o.getProcInstId())))).collect(Collectors.toList());
        try {
            //删除运行中的流程
            businessesDealing.forEach(b -> {
                String id = b.getProcInstId();
                runtimeService.deleteProcessInstance(id, userId);
            });
            //删除结束的流程
            businessesPass.forEach(b -> {
                String id = b.getProcInstId();
                historyService.deleteHistoricProcessInstance(id);
            });
        }catch (Exception e) {
            log.error("删除审批流程失败："+e);
            throw new BusinessException("删除审批流程失败，原因："+e.getMessage());
        }
        businessService.deleteBizBusinessByIds(ids);
        log.info("删除审批流程方法-执行结束！");
        return R.ok();
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
