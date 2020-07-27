package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActStartProcessVo;
import com.cloud.activiti.service.IActOmsProductionOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ProductOrderConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private RemoteUserService remoteUserService;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private IActTaskService actTaskService;
    @Autowired
    private RepositoryService repositoryService;
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
                        initBusiness(processDefinitionAct.getId(),processDefinitionAct.getName(),a.getOrderId(),a.getOrderCode(), actBusinessVo.getUserId(),actBusinessVo.getTitle());
                bizBusinessService.insertBizBusiness(business);
                Map<String, Object> variables = Maps.newHashMap();
                bizBusinessService.startProcess(business, variables);
            });
        } else {
            log.error("开启审批流失败，传入参数为空！");
            throw new BusinessException("开启审批流失败，传入参数为空！");
        }
        return R.ok("提交成功！");
    }

    @Override
    public R audit(BizAudit bizAudit, long userId) {
        log.info(StrUtil.format("ZN认证审核：参数为{}", bizAudit.toString()));
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            log.error(StrUtil.format("(ZN认证)流程业务表数据为空,id参数为{}", bizAudit.getBusinessKey()));
            return R.error("流程业务表数据为空！");
        }
        //查询物耗表信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (omsProductionOrder == null) {
            log.error(StrUtil.format("(ZN认证)排产订单表数据为空,id(排产订单)参数为{}", bizBusiness.getTableId()));
            return R.error("未找到排产订单业务数据！");
        }
        //审批结果
        Boolean result = false;
        if (bizAudit.getResult().intValue() == 2) {
            result = true;
        }
        //根据订单号查询处在流程中的审批流程
        BizBusiness business = new BizBusiness();
        business.setOrderNo(bizBusiness.getOrderNo());
        business.setStatus(1);
        List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessList(business);
        if (result) {
            //审批通过
            if (BeanUtil.isNotEmpty(bizBusinesses) && bizBusinesses.size() <= 1) {
                omsProductionOrder.setStatus(ProductOrderConstants.AUDIT_STATUS_TWO);
            }
        } else {
            //审批驳回
            omsProductionOrder.setStatus(ProductOrderConstants.AUDIT_STATUS_THREE);

        }
        //更新
        R r = remoteProductionOrderService.editSave(omsProductionOrder);
        if (r.isSuccess()) {
            //审批 推进工作流
            return actTaskService.audit(bizAudit, userId);
        }else{
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
