package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.consts.ActivitiTableNameConstants;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActStartProcessVo;
import com.cloud.activiti.mail.MailService;
import com.cloud.activiti.service.IActSmsQualityScrapOrderService;
import com.cloud.activiti.service.IActSmsRawScrapOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.enums.QualityScrapOrderStatusEnum;
import com.cloud.settle.enums.RawScrapOrderIsMaterialObjectEnum;
import com.cloud.settle.enums.RawScrapOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsQualityScrapOrderLogService;
import com.cloud.settle.feign.RemoteSmsQualityScrapOrderService;
import com.cloud.settle.feign.RemoteSmsRawScrapOrderService;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ActSmsQualityScrapOrderServiceImpl implements IActSmsQualityScrapOrderService {
    @Autowired
    private IActTaskService actTaskService;
    @Autowired
    private IBizBusinessService bizBusinessService;
    @Autowired
    private RemoteSmsQualityScrapOrderService remoteSmsQualityScrapOrderService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private MailService mailService;
    @Autowired
    private RemoteSmsQualityScrapOrderLogService remoteSmsQualityScrapOrderLogService;

    @Override
    @GlobalTransactional
    public R startAct(ActBusinessVo actBusinessVo) {
        if (BeanUtil.isNotEmpty(actBusinessVo)) {
            R keyMap = actTaskService.getByKey(actBusinessVo.getKey());
            if (!keyMap.isSuccess()) {
                log.error("根据Key获取最新版流程实例失败：" + keyMap.get("msg"));
                throw new BusinessException("根据Key获取最新版流程实例失败!");
            }
            ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
            List<ActStartProcessVo> list = actBusinessVo.getProcessVoList();
            //插入流程物业表  并开启流程
            list.forEach(a -> {
                BizBusiness business =
                        initBusiness(processDefinitionAct.getId()
                                , processDefinitionAct.getName(), a.getOrderId(), a.getOrderCode()
                                , actBusinessVo.getUserId(), actBusinessVo.getTitle(), actBusinessVo.getUserName());
                bizBusinessService.insertBizBusiness(business);
                //非会签
                Map<String, Object> variables = Maps.newHashMap();
                bizBusinessService.startProcess(business, variables, a.getUserIds());
            });
        } else {
            log.error("开启审批流失败，传入参数为空！");
            throw new BusinessException("开启审批流失败，传入参数为空！");
        }
        return R.ok("提交成功！");
    }

    /**
     * 根据业务key获取数据
     *
     * @param businessKey
     * @return smsSupplementaryOrder
     * @author cs
     */
    @Override
    public R getBizInfoByTableId(String businessKey) {
        //查询流程业务表
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            //根据流程业务表 tableId 查询业务表信息
            R smsScrapOrderMap = remoteSmsQualityScrapOrderService.get(Long.valueOf(business.getTableId()));
            if (!smsScrapOrderMap.isSuccess()) {
                log.error("查询业务数据记录失败,原因："+smsScrapOrderMap.get("msg"));
                return R.error("查询业务数据记录失败!");
            }
            SmsQualityScrapOrder smsQualityScrapOrder = smsScrapOrderMap.getData(SmsQualityScrapOrder.class);
            R orderLogMap = remoteSmsQualityScrapOrderLogService.getByQualityId(smsQualityScrapOrder.getId());
            if (!orderLogMap.isSuccess()) {
                log.error("查询业务数据-申诉记录失败,原因："+orderLogMap.get("msg"));
                return R.error("查询业务数据-申诉记录失败!");
            }
            SmsQualityScrapOrderLog smsQualityScrapOrderLog = orderLogMap.getData(SmsQualityScrapOrderLog.class);
            R result = new R();
            result.put("procInstId",business.getProcInstId());
            result.put("data", smsQualityScrapOrder);
            result.put("dataLog",smsQualityScrapOrderLog);
            return result;
        }
        return R.error("no record");
    }

    /**
     * 审批流程 物耗申请单逻辑
     * 待加全局事务
     *
     * @param bizAudit
     * @param userId
     * @return R
     */
    @Override
    @GlobalTransactional
    public R audit(BizAudit bizAudit, long userId) {
        String taskId = bizAudit.getTaskId();
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("result", bizAudit.getResult());
        variables.put("comment", bizAudit.getComment());
        variables.put("taskIdVar", taskId);
        variables.put("bizBusinessId", bizAudit.getBusinessKey().toString());

        taskService.complete(taskId, variables);
        bizAudit = redisUtils.get(StrUtil.format("bizAudit{}{}", bizAudit.getBusinessKey().toString(), taskId),BizAudit.class);
        Set<String> userIds = redisUtils.get(StrUtil.format("userIds{}{}", bizAudit.getBusinessKey().toString(), taskId), Set.class);
        return actTaskService.auditCandidateUserMul(bizAudit, userId,userIds);
    }

    @Override
    public R auditLogic(BizAudit bizAudit, long userId){
        log.info(StrUtil.format("质量部报废申请审核：参数为{}", bizAudit.toString()));
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            log.error(StrUtil.format("(质量部报废)流程业务表数据为空,id参数为{}", bizAudit.getBusinessKey()));
            return R.error("流程业务表数据为空！");
        }
        //查询原材料报废表信息
        R rawScrapMap = remoteSmsQualityScrapOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (!rawScrapMap.isSuccess()) {
            log.error(StrUtil.format("(质量部报废)质量部报废表数据查询失败,id(质量部报废)参数为{}", bizBusiness.getTableId()));
            return R.error("未找到质量部报废业务数据！");
        }
        SmsQualityScrapOrder smsQualityScrapOrder = rawScrapMap.getData(SmsQualityScrapOrder.class);
        //审批结果
        Boolean result = false;
        if (bizAudit.getResult().intValue() == 2) {
            result = true;
        }
        //判断下级审批  修改状态
        if (result) {
            //审批通过
            smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_WXJS.getCode());
        } else {
            //审批驳回
            smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_SHBH.getCode());
        }
        //更新质量部报废订单
        R r = remoteSmsQualityScrapOrderService.updateAct(smsQualityScrapOrder,bizAudit.getResult(),bizAudit.getComment(),bizAudit.getAuditor());
        if (r.isSuccess()) {
            //审批 推进工作流
            R rResult = new R();
            rResult.put("bizAudit",bizAudit);
            rResult.put("userId",userId);
            return rResult;
        }else{
            throw new BusinessException(r.getStr("msg"));
        }
    }
    /**
     * Description:  组织数据
     * Param: [procDefId, procName, orderId, orderCode, userId, title]
     * return: com.cloud.activiti.domain.BizBusiness
     * Author: ltq
     * Date: 2020/6/24
     */
    private BizBusiness initBusiness(String procDefId, String procName, String orderId, String orderCode, long userId, String title, String userName) {

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
        business.setTableName(ActivitiTableNameConstants.ACTIVITI_TABLE_NAME_RAW_SCRAP);
        return business;
    }

}
