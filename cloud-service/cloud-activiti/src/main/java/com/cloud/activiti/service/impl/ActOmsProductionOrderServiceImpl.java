package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.constant.ActProcessContants;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActProcessEmailUserVo;
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
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.order.feign.RemoteProductionOrderDetailService;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.system.domain.entity.SysUser;
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
import org.apache.xmlbeans.impl.common.ConcurrentReaderHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
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
    @Autowired
    private RemoteProductionOrderDetailService remoteProductionOrderDetailService;
    /**
     * Description:  开启审批流
     * Param: [key, orderId, orderCode, userId, title]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    @Override
    @GlobalTransactional
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

    /**
     * Description:  定时任务开启排产订单审批流程
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/10/19
     */
    @Override
    @GlobalTransactional
    public R timeCheckProductOrderAct() {
        //1、获取初始化中的排产订单数据
        R productOrderMap = remoteProductionOrderService.selectByStatusAct();
        if (!productOrderMap.isSuccess()) {
            log.error("初始化排产订单状态-获取排产订单数据失败，原因："+productOrderMap.get("msg"));
            return R.error("初始化排产订单状态-获取排产订单数据失败，原因："+productOrderMap.get("msg"));
        }
        List<OmsProductionOrder> omsProductionOrders =
                productOrderMap.getCollectData(new TypeReference<List<OmsProductionOrder>>() {});
        if (ObjectUtil.isEmpty(omsProductionOrders) || omsProductionOrders.size() <= 0) {
            log.info("本次初始化排产订单状态-获取排产订单数据为空！");
            return R.ok("本次初始化排产订单状态-获取排产订单数据为空！");
        }
        //获取排产订单明细，设置排产订单的状态
        List<String> orderCodes = omsProductionOrders
                .stream().map(OmsProductionOrder::getOrderCode).collect(Collectors.toList());
        R detailMap = remoteProductionOrderDetailService.selectDetailByOrderAct(orderCodes);
        if (!detailMap.isSuccess()) {
            log.info("定时任务开启排产订单审批流程，查询排产订单明细失败！");
            return R.error("定时任务开启排产订单审批流程，查询排产订单明细失败！");
        }
        List<OmsProductionOrderDetail> omsProductionOrderDetails =
                detailMap.getCollectData(new TypeReference<List<OmsProductionOrderDetail>>() {});
        Map<String,List<OmsProductionOrderDetail>> orderDetailMap =
                CollectionUtil.isEmpty(omsProductionOrderDetails) ? new HashMap<>() : omsProductionOrderDetails
                        .stream().collect(Collectors.groupingBy(OmsProductionOrderDetail :: getProductOrderCode));

        //2、调用order服务，校验排产订单的审批流
        R businessVoMap = remoteProductionOrderService.checkProductOrderAct(omsProductionOrders);
        if (!businessVoMap.isSuccess()) {
            log.error("调用order服务，校验排产订单的审批闸口失败，原因："+businessVoMap.get("msg"));
            return R.error("调用order服务，校验排产订单的审批闸口失败，原因："+businessVoMap.get("msg"));
        }
        List<ActBusinessVo> businessVoList =
                businessVoMap.getCollectData(new TypeReference<List<ActBusinessVo>>() {});
        //3、开启审批流程
        //判断是否有排产订单进入审批流，没有直接更新状态
        if (ObjectUtil.isEmpty(businessVoList) || businessVoList.size() <= 0) {
            omsProductionOrders.forEach(o -> {
                List<OmsProductionOrderDetail> details = orderDetailMap.get(o.getOrderCode());
                if (ObjectUtil.isEmpty(details) || details.size() <= 0) {
                    o.setStatus(ProductOrderConstants.STATUS_THREE);
                } else {
                    o.setStatus(ProductOrderConstants.STATUS_ZERO);
                }
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ZERO);
            });
            R updateOrderMap = remoteProductionOrderService.updateBatchByPrimary(omsProductionOrders);
            if (!updateOrderMap.isSuccess()) {
                log.error("定时任务开启排产订单审批流程，更新排产订单状态失败，原因："+updateOrderMap.get("msg"));
                throw new BusinessException("定时任务开启排产订单审批流程，更新排产订单状态失败，原因："+updateOrderMap.get("msg"));
            }
            return R.ok();
        }
        R startActMap = startActProcessAct(businessVoList);
        if (!startActMap.isSuccess()) {
            log.error("定时任务开启排产订单审批流程失败，原因："+startActMap.get("msg"));
            return R.error("定时任务开启排产订单审批流程失败，原因："+startActMap.get("msg"));
        }
        //4、更新排产订单的状态
        Set<String> orderIdSet = new HashSet<>();
        List<ActProcessEmailUserVo> emailUserVos = new ArrayList<>();
        businessVoList.forEach(b -> {
            List<ActStartProcessVo> processVos = b.getProcessVoList();
            Set<String> orderIds = processVos.stream().map(ActStartProcessVo::getOrderId).collect(Collectors.toSet());
            orderIdSet.addAll(orderIds);
            //获取邮件通知内容
            emailUserVos.addAll(b.getProcessEmailUserVoList());
        });
        omsProductionOrders.forEach(o -> {
            List<OmsProductionOrderDetail> details = orderDetailMap.get(o.getOrderCode());
            if (ObjectUtil.isEmpty(details) || details.size() <= 0) {
                o.setStatus(ProductOrderConstants.STATUS_THREE);
            } else {
                o.setStatus(ProductOrderConstants.STATUS_ZERO);
            }

            if (orderIdSet.contains(o.getId().toString())) {
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ONE);
            } else {
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ZERO);
            }
        });
        R updateOrderMap = remoteProductionOrderService.updateBatchByPrimary(omsProductionOrders);
        if (!updateOrderMap.isSuccess()) {
            log.error("定时任务开启排产订单审批流程，更新排产订单状态失败，原因："+updateOrderMap.get("msg"));
            throw new BusinessException("定时任务开启排产订单审批流程，更新排产订单状态失败，原因："+updateOrderMap.get("msg"));
        }
        //5、邮件通知
        emailUserVos.forEach(e ->
            mailService.sendTextMail(e.getEmail(), e.getTitle(), e.getContext())
        );
        return R.ok();
    }

    public R startActProcessAct(List<ActBusinessVo> actBusinessVoList) {
        actBusinessVoList.forEach(actBusinessVo -> {
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
                    Example example = new Example(BizBusiness.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("orderNo",a.getOrderCode());
                    criteria.andEqualTo("procDefKey",actBusinessVo.getKey());
                    List<BizBusiness> businesses = bizBusinessService.selectByExample(example);
                    if (!ObjectUtil.isNotEmpty(businesses) || businesses.size() <= 0) {
                        BizBusiness business =
                                initBusiness(processDefinitionAct.getId()
                                        ,processDefinitionAct.getName(),a.getOrderId(),a.getOrderCode()
                                        , a.getUserId(),actBusinessVo.getTitle(),a.getUserName());
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
                    }
                });
            } else {
                log.error("开启审批流失败，传入参数为空！");
                throw new BusinessException("开启审批流失败，传入参数为空！");
            }
        });
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
        if (!CollectionUtil.isNotEmpty(tasks)) {
            log.info("排产订单："+bizBusiness.getOrderNo()+","+bizBusiness.getProcDefKey()+"审批流程最后一个节点！");
            //判断其他审批流程
            if (!CollectionUtil.isNotEmpty(bizBusinesses)) {
                log.info("排产订单："+bizBusiness.getOrderNo()+"无进行中审批流！");
                if (!CollectionUtil.isNotEmpty(businesses)) {
                    log.info("排产订单："+bizBusiness.getOrderNo()+"无驳回审批流！");
                    omsProductionOrder.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_TWO);
                    if (bizBusiness.getProcDefKey().equals(ActProcessContants.ACTIVITI_ADD_REVIEW)
                            || bizBusiness.getProcDefKey().equals(ActProcessContants.ACTIVITI_ZN_REVIEW)) {
                        omsProductionOrder.setStatus(ProductOrderConstants.STATUS_FOUR);
                    }
                } else {
                    //审批驳回
                    omsProductionOrder.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_THREE);
                }
                //更新排产订单的状态
                R r = remoteProductionOrderService.editSave(omsProductionOrder);
                if (!r.isSuccess()) {
                    log.error("排产订单审批流程更新排产订单的状态失败，原因："+r.get("msg"));
                    throw new BusinessException("排产订单审批流程更新排产订单的状态失败，原因："+r.get("msg"));
                }
            }
        }
        return R.ok();

    }

    @Override
    public R getBizInfoByTableId(String businessKey) {
        //查询流程业务表
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            R resultMap = new R();
            //根据流程业务表 tableId 查询业务表信息
            OmsProductionOrder omsProductionOrder = remoteProductionOrderService.get(Long.valueOf(business.getTableId()));
            resultMap.put("data",omsProductionOrder);
            resultMap.put("procInstId",business.getProcInstId());
            return resultMap;
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
