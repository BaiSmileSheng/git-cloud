package com.cloud.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.consts.ActivitiProDefKeyConstants;
import com.cloud.activiti.consts.ActivitiProTitleConstants;
import com.cloud.activiti.consts.ActivitiTableNameConstants;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.mail.MailService;
import com.cloud.activiti.service.IActSmsSupplementaryOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.EmailConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsSupplementaryOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ActSmsSupplementaryOrderServiceImpl implements IActSmsSupplementaryOrderService {
    @Autowired
    private IBizBusinessService bizBusinessService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private RemoteSmsSupplementaryOrderService remoteSmsSupplementaryOrderService;
    @Autowired
    private IActTaskService actTaskService;
    @Autowired
    private MailService mailService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 开启流程 物耗申请单逻辑  新增、编辑提交时开启
     *
     * @param smsSupplementaryOrder
     * @return R
     */
    @Override
    @GlobalTransactional
    public R startAct(SmsSupplementaryOrder smsSupplementaryOrder, SysUser sysUser,String procDefId,String procName) {
        log.info(StrUtil.format("物耗申请开启流程（新增、编辑）：参数为{}", smsSupplementaryOrder.toString()));
        if (smsSupplementaryOrder.getId() == null) {
            //新增提交  校验  获取数据  插入数据  开启流程
            smsSupplementaryOrder.setCreateBy(sysUser.getLoginName());
            smsSupplementaryOrder.setSubmitDate(DateUtil.date());
            smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode());
            R rAdd = remoteSmsSupplementaryOrderService.addSave(smsSupplementaryOrder);
            if (!rAdd.isSuccess()) {
                throw new BusinessException(rAdd.getStr("msg"));
            }
            Long id = Long.valueOf(rAdd.get("data").toString());
            smsSupplementaryOrder.setId(id);
        } else {
            //修改提交  校验  更新数据  开启流程
            smsSupplementaryOrder.setUpdateBy(sysUser.getLoginName());
            smsSupplementaryOrder.setSubmitDate(DateUtil.date());
            smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode());
            R rUpdate = remoteSmsSupplementaryOrderService.editSave(smsSupplementaryOrder);
            if (!rUpdate.isSuccess()) {
                throw new BusinessException(rUpdate.getStr("msg"));
            }
        }
        //插入流程物业表  并开启流程
        smsSupplementaryOrder.setProcDefId(procDefId);
        smsSupplementaryOrder.setProcName(procName);
        SmsSupplementaryOrder smsSupplementaryOrderCheck = remoteSmsSupplementaryOrderService.get(smsSupplementaryOrder.getId());
        //Y61校验数据准确性
        smsSupplementaryOrderCheck.setSapFlag(SapConstants.SAP_Y61_FLAG_JY);
        R rCheck = remoteSmsSupplementaryOrderService.autidSuccessToSAPY61(smsSupplementaryOrderCheck);
        if (!rCheck.isSuccess()) {
            throw new BusinessException(rCheck.getStr("msg"));
        }
        smsSupplementaryOrder.setStuffNo(smsSupplementaryOrderCheck.getStuffNo());
        BizBusiness business = initBusiness(smsSupplementaryOrder, sysUser.getUserId());
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //指定下一审批人
        R rUser = remoteUserService.selectUserByFactoryCodeAndPurchaseCodeAndRoleKey(smsSupplementaryOrderCheck.getFactoryCode()
        ,smsSupplementaryOrderCheck.getPurchaseGroupCode(), RoleConstants.ROLE_KEY_JIT);
        if(!rUser.isSuccess()){
            log.error("物耗审批开启失败，下一级审核人为空！");
            throw new BusinessException("物耗审批开启失败，下一级审核人为空！");
        }
        List<SysUserVo> users=rUser.getCollectData(new TypeReference<List<SysUserVo>>() {});
        //发送邮件通知
        try {
            sendEmail(smsSupplementaryOrder.getStuffNo(),users);
        } catch (Exception e) {
            log.error("物耗审批发送邮件失败!{}", e);
        }
        Set<String> userIds = users.stream().map(user->user.getUserId().toString()).collect(Collectors.toSet());
        bizBusinessService.startProcess(business, variables,userIds);
        return R.ok("提交成功！");
    }

    /**
     * 发送邮件
     * @param stuffNo
     * @param sysUserVoList
     */
    private void sendEmail(String stuffNo, List<SysUserVo> sysUserVoList) {
        //校验邮件
        for(SysUserVo sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            if(StringUtils.isBlank(email)){
                throw new  BusinessException("用户"+sysUserVo.getUserName()+"邮箱不存在");
            }
        }
        //发送邮件
        for(SysUserVo sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            String subject = "物耗申请单审批";
            String content = "您有一条待办消息要处理!" + "物耗申请单 单号:" + stuffNo +  EmailConstants.ORW_URL;
            mailService.sendTextMail(email,subject,content);
        }
    }

    @Override
    @GlobalTransactional
    public R startActList(List<SmsSupplementaryOrder> smsSupplementaryOrders, SysUser sysUser) {
        R keyMap = actTaskService.getByKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_SUPPLEMENTARY_TEST);
        if (!keyMap.isSuccess()) {
            log.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        smsSupplementaryOrders.forEach(smsSupplementaryOrder->{
            R r = startAct(smsSupplementaryOrder, sysUser, processDefinitionAct.getId(), processDefinitionAct.getName());
            if (!r.isSuccess()) {
                throw new BusinessException(r.getStr("msg"));
            }
        });
        return R.ok();
    }

    /**
     * 开启流程 物耗申请单逻辑  列表提交时开启
     *
     * @param smsSupplementaryOrder
     * @param userId
     * @return
     */
    @Override
    @GlobalTransactional
    public R startActOnlyForList(SmsSupplementaryOrder smsSupplementaryOrder, long userId) {
        log.info(StrUtil.format("物耗申请开启流程（列表）：参数为{}", smsSupplementaryOrder.toString()));
        //列表提交  更新数据  开启流程
        Long id = smsSupplementaryOrder.getId();
        if (id == null) {
            return R.error("id不能为空！");
        }
        SmsSupplementaryOrder smsSupplementaryOrderCheck = remoteSmsSupplementaryOrderService.get(id);
        if (smsSupplementaryOrder == null) {
            log.error(StrUtil.format("(物耗)物耗表数据为空,id(物耗)参数为{}", id));
            return R.error("未找到物耗业务数据！");
        }
        if (!SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode().equals(smsSupplementaryOrderCheck.getStuffStatus())) {
            log.error(StrUtil.format("(物耗)只有待提交状态数据可以提交!原状态参数为{}", smsSupplementaryOrderCheck.getStuffStatus()));
            return R.error("只有待提交状态数据可以提交！");
        }
        //Y61校验数据准确性
        smsSupplementaryOrderCheck.setSapFlag(SapConstants.SAP_Y61_FLAG_JY);
        R rCheck = remoteSmsSupplementaryOrderService.autidSuccessToSAPY61(smsSupplementaryOrderCheck);
        if (!rCheck.isSuccess()) {
            return R.error(rCheck.getStr("msg"));
        }
        //更新数据
        smsSupplementaryOrder.setSubmitDate(DateUtil.date());
        smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode());
        R rUpdate = remoteSmsSupplementaryOrderService.update(smsSupplementaryOrder);
        if (!rUpdate.isSuccess()) {
            throw new BusinessException(rUpdate.getStr("msg"));
        }
        R keyMap = actTaskService.getByKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_SUPPLEMENTARY_TEST);
        if (!keyMap.isSuccess()) {
            log.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        smsSupplementaryOrder.setProcName(processDefinitionAct.getName());
        smsSupplementaryOrder.setProcDefId(processDefinitionAct.getId());
        //插入流程物业表  并开启流程
        smsSupplementaryOrder.setStuffNo(smsSupplementaryOrderCheck.getStuffNo());
        BizBusiness business = initBusiness(smsSupplementaryOrder, userId);
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //指定下一审批人
        R rUser = remoteUserService.selectUserByFactoryCodeAndPurchaseCodeAndRoleKey(smsSupplementaryOrderCheck.getFactoryCode()
                ,smsSupplementaryOrderCheck.getPurchaseGroupCode(), RoleConstants.ROLE_KEY_JIT);
        if(!rUser.isSuccess()){
            log.error("物耗审批开启失败，下一级审核人为空！");
            throw new BusinessException("物耗审批开启失败，下一级审核人为空！");
        }
        List<SysUserVo> users=rUser.getCollectData(new TypeReference<List<SysUserVo>>() {});
        try {
            sendEmail(smsSupplementaryOrderCheck.getStuffNo(),users);
        } catch (Exception e) {
            log.error("物耗审批发送邮件失败!{}", e);
        }

        Set<String> userIds = users.stream().map(user->user.getUserId().toString()).collect(Collectors.toSet());
        bizBusinessService.startProcess(business, variables,userIds);
        return R.ok("提交成功！");
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
        log.info(StrUtil.format("物耗申请审核：参数为{}", bizAudit.toString()));
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            log.error(StrUtil.format("(物耗)流程业务表数据为空,id参数为{}", bizAudit.getBusinessKey()));
            return R.error("流程业务表数据为空！");
        }
        //查询物耗表信息
        SmsSupplementaryOrder smsSupplementaryOrder = remoteSmsSupplementaryOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (smsSupplementaryOrder == null) {
            log.error(StrUtil.format("(物耗)物耗表数据为空,id(物耗)参数为{}", bizBusiness.getTableId()));
            return R.error("未找到物耗业务数据！");
        }
        //审批结果
        Boolean result = false;
        if (bizAudit.getResult().intValue() == 2) {
            result = true;
        }
        //判断下级审批  修改状态
        //1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、
        if (result) {
            //审批通过
            if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                //审批完成
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode());
            } else {
                log.error(StrUtil.format("(物耗)物耗审批通过状态错误：{}", smsSupplementaryOrder.getStuffStatus()));
                throw new BusinessException("此状态数据不允许审核！");
            }
        } else {
            //审批驳回
            if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITBH.getCode());
            } else {
                log.error(StrUtil.format("(物耗)物耗审批驳回状态错误：{}", smsSupplementaryOrder.getStuffStatus()));
                throw new BusinessException("此状态数据不允许审核！");
            }
        }
        //JIT审核通过传SAP
        if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
            smsSupplementaryOrder.setSapFlag(SapConstants.SAP_Y61_FLAG_GZ);
            R rSAP = remoteSmsSupplementaryOrderService.autidSuccessToSAPY61(smsSupplementaryOrder);
            if (!rSAP.isSuccess()) {
                throw new BusinessException(rSAP.getStr("msg"));
            }
            smsSupplementaryOrder = rSAP.getData(SmsSupplementaryOrder.class);
        }
        R r = remoteSmsSupplementaryOrderService.update(smsSupplementaryOrder);
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
            SmsSupplementaryOrder smsSupplementaryOrder = remoteSmsSupplementaryOrderService.get(Long.valueOf(business.getTableId()));
            R result = new R();
            result.put("procInstId",business.getProcInstId());
            result.put("data", smsSupplementaryOrder);
            return result;
        }
        return R.error("no record");
    }

    /**
     * biz构造业务信息
     *
     * @param smsSupplementaryOrder
     * @return
     * @author cs
     */
    private BizBusiness initBusiness(SmsSupplementaryOrder smsSupplementaryOrder, long userId) {
        BizBusiness business = new BizBusiness();
        business.setOrderNo(smsSupplementaryOrder.getStuffNo());
        business.setTableId(smsSupplementaryOrder.getId().toString());
        business.setTableName(ActivitiTableNameConstants.ACTIVITI_TABLE_NAME_SUPPLEMENTARY);
        business.setProcDefId(smsSupplementaryOrder.getProcDefId());
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SUPPLEMENTARY_TEST);
        business.setProcName(smsSupplementaryOrder.getProcName());
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}
