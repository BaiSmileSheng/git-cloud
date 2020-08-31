package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.constant.ActProcessContants;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActStartProcessVo;
import com.cloud.activiti.mail.MailService;
import com.cloud.activiti.service.IActOmsProductionOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.EmailConstants;
import com.cloud.common.constant.ProductOrderConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:  排产订单审批流程
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/23
 */
@Service
@Slf4j
public class ActOmsProductionOrderServiceImpl implements IActOmsProductionOrderService {
    @Autowired
    private IBizBusinessService bizBusinessService;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private IActTaskService actTaskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private MailService mailService;
    /**
     * Description:  开启审批流
     * Param: [key, orderId, orderCode, userId, title]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    @Override
    public R startActProcess(ActBusinessVo actBusinessVo) {
        if (BeanUtil.isNotEmpty(actBusinessVo)) {
            R keyMap = getByKey(actBusinessVo.getKey());
            if (!keyMap.isSuccess()) {
                log.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
                throw new BusinessException("根据Key获取最新版流程实例失败!");
            }
            ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
            List<ActStartProcessVo> list = actBusinessVo.getProcessVoList();
            //插入流程物业表  并开启流程
            list.forEach(a ->{
                BizBusiness business =
                        initBusiness(processDefinitionAct.getId()
                                ,processDefinitionAct.getName(),a.getOrderId(),a.getOrderCode()
                                , actBusinessVo.getUserId(),actBusinessVo.getTitle(),actBusinessVo.getUserName());
                bizBusinessService.insertBizBusiness(business);
                if (actBusinessVo.getKey().equals(ActProcessContants.ACTIVITI_ADD_REVIEW)
                        || actBusinessVo.getKey().equals(ActProcessContants.ACTIVITI_OVERDUE_STOCK_ORDER_REVIEW)) {
                    //会签流程,暂时只有T+2追加订单、超期库存是会签
                    Map<String, Object> variables = Maps.newHashMap();
                    variables.put("signList",a.getUserIds());
                    bizBusinessService.startProcessForHuiQian(business, variables);
                } else {
                    //非会签
                    Map<String, Object> variables = Maps.newHashMap();
                    bizBusinessService.startProcess(business, variables,a.getUserIds());
                }
            });
        } else {
            log.error("开启审批流失败，传入参数为空！");
            throw new BusinessException("开启审批流失败，传入参数为空！");
        }
        return R.ok("提交成功！");
    }

    @Override
    @GlobalTransactional
    public R audit(BizAudit bizAudit, long userId) {
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        log.info(StrUtil.format("排产订单审批流程-"+bizBusiness.getProcName()+"：参数为{}", bizAudit.toString()));
        if (bizBusiness == null) {
            log.error(StrUtil.format("排产订单审批流程-"+bizBusiness.getProcName()+",id参数为{}", bizAudit.getBusinessKey()));
            return R.error("流程业务表数据为空！");
        }
        //查询排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (omsProductionOrder == null) {
            log.error(StrUtil.format("排产订单审批流程-"+bizBusiness.getProcName()+",id参数为{}", bizBusiness.getTableId()));
            return R.error("未找到排产订单业务数据！");
        }
        //判断审批流，调用不同的审批推进方法
        if (bizBusiness.getProcDefKey().equals(ActProcessContants.ACTIVITI_ADD_REVIEW)
                || bizBusiness.getProcDefKey().equals(ActProcessContants.ACTIVITI_OVERDUE_STOCK_ORDER_REVIEW)) {
            //T+2追加订单审批流程- 会签推进
            actTaskService.audit(bizAudit, userId,null);
        } else if (bizBusiness.getProcDefKey().equals(ActProcessContants.ACTIVITI_OVERDUE_NOT_CLOSE_ORDER_REVIEW)){
            //超期未关闭订单审批流程 - 非会签推进，存在下一节点
            //根据生产工厂、角色查询用户
            R sysUserR = remoteUserService.selectUserByMaterialCodeAndRoleKey(omsProductionOrder.getProductFactoryCode(), RoleConstants.ROLE_KEY_XWZ);
            if(!sysUserR.isSuccess()){
                log.error("超期未关闭订单审批流程，获取下一节点审批人失败 factoryCode:{},roleKey:{}",omsProductionOrder.getProductFactoryCode(), RoleConstants.ROLE_KEY_XWZ);
                throw new BusinessException("超期未关闭订单审批流程，获取下一节点审批人失败" + sysUserR.get("msg").toString());
            }
            List<SysUserVo> sysUserVoList = sysUserR.getCollectData(new TypeReference<List<SysUserVo>>() {});
            Set<String> userIdSet = sysUserVoList.stream().map(user -> user.getUserId().toString()).collect(Collectors.toSet());
            actTaskService.auditCandidateUser(bizAudit, userId,userIdSet);
            //邮件通知下一节点审批人
            //发送邮件
            sysUserVoList.forEach(u -> {
                String email = u.getEmail();
                String context = u.getUserName() + EmailConstants.OVERDUE_NOT_CLOSE_ORDER_REVIEW_CONTEXT + EmailConstants.ORW_URL;
                mailService.sendTextMail(email, EmailConstants.TITLE_OVERDUE_NOT_CLOSE_ORDER_REVIEW, context);
            });
        } else {
            //审批 推进工作流
            actTaskService.auditCandidateUser(bizAudit, userId,null);
        }
        //根据订单号查询处在流程中的审批流程
        BizBusiness business = new BizBusiness();
        business.setOrderNo(bizBusiness.getOrderNo());
        business.setResult(ActivitiConstant.RESULT_DEALING);
        List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessList(business);
        business.setResult(ActivitiConstant.RESULT_FAIL);
        List<BizBusiness> businesses = bizBusinessService.selectBizBusinessList(business);
        //复查
        bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(bizBusiness.getProcInstId()).list();
        if (null == tasks || tasks.size() <= 0) {
            //判断其他审批流程
            if (BeanUtil.isNotEmpty(bizBusinesses) && bizBusinesses.size() <= 1 && businesses.size() <= 0) {
                omsProductionOrder.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_TWO);
                if (bizBusiness.getProcDefKey().equals(ActProcessContants.ACTIVITI_ADD_REVIEW)) {
                    omsProductionOrder.setStatus(ProductOrderConstants.STATUS_FOUR);
                }
            } else if (BeanUtil.isNotEmpty(bizBusinesses) && bizBusinesses.size() <= 1 && businesses.size() > 0){
                //审批驳回
                omsProductionOrder.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_THREE);
            }
        }
        //更新
        R r = remoteProductionOrderService.editSave(omsProductionOrder);
        if (r.isSuccess()) {
            return R.ok();
        } else {
            throw new BusinessException(r.getStr("msg"));
        }
    }

    @Override
    public R getBizInfoByTableId(String businessKey) {
        //查询流程业务表
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            //根据流程业务表 tableId 查询业务表信息
            OmsProductionOrder omsProductionOrder = remoteProductionOrderService.get(Long.valueOf(business.getTableId()));
            return R.data(omsProductionOrder);
        }
        return R.error("no record");
    }

    /**
     * Description:  根据Key查询最新版本流程
     * Param: [key]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    private R getByKey(String key) {
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
     * Author: ltq
     * Date: 2020/6/24
     */
    private BizBusiness initBusiness(String procDefId,String procName,String orderId,String orderCode, long userId,String title,String userName) {

        BizBusiness business = new BizBusiness();
        business.setOrderNo(orderCode);
        business.setTableId(orderId);
        business.setProcDefId(procDefId);
        business.setTitle(title);
        business.setProcName(procName);
        business.setUserId(userId);
        business.setApplyer(userName);
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}
