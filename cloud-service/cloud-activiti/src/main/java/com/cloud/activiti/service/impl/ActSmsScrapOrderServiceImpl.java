package com.cloud.activiti.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.consts.ActivitiProDefKeyConstants;
import com.cloud.activiti.consts.ActivitiProTitleConstants;
import com.cloud.activiti.consts.ActivitiTableNameConstants;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.mail.MailService;
import com.cloud.activiti.service.IActSmsScrapOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.EmailConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.IsPayEnum;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsScrapOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ActSmsScrapOrderServiceImpl implements IActSmsScrapOrderService {
    @Autowired
    private IBizBusinessService bizBusinessService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private RemoteSmsScrapOrderService remoteSmsScrapOrderService;
    @Autowired
    private IActTaskService actTaskService;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private MailService mailService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 开启流程 报废申请单逻辑(编辑、新增提交)
     * 待加全局事务
     *
     * @param smsScrapOrder
     * @return R
     */
    @Override
    @GlobalTransactional
    public R startAct(SmsScrapOrder smsScrapOrder, SysUser sysUser) {
        log.info(StrUtil.format("报废申请开启流程（编辑、新增）：参数为{}", smsScrapOrder.toString()));
        Preconditions.checkArgument(StrUtil.isNotEmpty(smsScrapOrder.getIsPay()),"是否买单为空，不允许提交！");
        if (IsPayEnum.IS_PAY_NO.getCode().equals(smsScrapOrder.getIsPay())) {
            //不买单
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLSH.getCode());
        }else{
            //买单
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSH.getCode());
        }
        smsScrapOrder.setSubmitDate(DateUtil.date());
        //判断状态是否是未提交，如果不是则抛出错误
        if (smsScrapOrder.getId() == null) {
            smsScrapOrder.setCreateBy(sysUser.getLoginName());
            R rAdd = remoteSmsScrapOrderService.addSave(smsScrapOrder);
            if (!rAdd.isSuccess()) {
                log.info("报废申请保存结果：{}", rAdd.toString());
                throw new BusinessException(rAdd.getStr("msg"));
            }
            Long id = Long.valueOf(rAdd.get("data").toString());
            smsScrapOrder.setId(id);
        } else {
            //编辑提交  更新数据
            smsScrapOrder.setUpdateBy(sysUser.getLoginName());
            R rUpdate = remoteSmsScrapOrderService.editSave(smsScrapOrder);
            if (!rUpdate.isSuccess()) {
                log.info("报废申请更新结果：{}", rUpdate.toString());
                throw new BusinessException(rUpdate.getStr("msg"));
            }
        }
        SmsScrapOrder smsScrapOrderCheck = remoteSmsScrapOrderService.get(smsScrapOrder.getId());
        smsScrapOrder.setScrapNo(smsScrapOrderCheck.getScrapNo());
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsScrapOrder, sysUser.getUserId());

        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //指定下一审批人
        Set<String> userIds = new HashSet<>();
        List<SysUser> mailUsers = new ArrayList<>();
        R omsProductionOrderResult = remoteProductionOrderService.selectByProdctOrderCode(smsScrapOrderCheck.getProductOrderCode());
        if (!omsProductionOrderResult.isSuccess()) {
            throw new BusinessException("订单信息不存在");
        }
        OmsProductionOrder omsProductionOrder = omsProductionOrderResult.getData(OmsProductionOrder.class);
        smsScrapOrder.setFactoryCode(omsProductionOrder.getProductFactoryCode());
        if (IsPayEnum.IS_PAY_NO.getCode().equals(smsScrapOrder.getIsPay())) {
            //不买单  各工厂质量经理审核
            R rUser = remoteUserService.selectUserByMaterialCodeAndRoleKey(smsScrapOrder.getFactoryCode()
                    ,  RoleConstants.ROLE_KEY_ZLGCS);
            if (!rUser.isSuccess()) {
                log.error("不买单报废提交失败，下一级审核人为空！");
                throw new BusinessException("不买单报废提交失败，下一级审核人为空！");
            }
            List<SysUserVo> users = rUser.getCollectData(new TypeReference<List<SysUserVo>>() {});
            userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.toSet());
            mailUsers = users.stream().map(user -> {
                SysUser u = SysUser.builder().email(user.getEmail()).userName(user.getUserName()).build();
                return u;
            }).collect(Collectors.toList());
        }else{
            //买单 由生产订单创建人审批
            String createBy = omsProductionOrder.getCreateBy();
            SysUser createUser=remoteUserService.selectSysUserByUsername(createBy);
            userIds = CollectionUtil.set(false, createUser.getUserId().toString());
            mailUsers.add(createUser);
        }
        bizBusinessService.startProcess(business, variables,userIds);
        //发送邮件通知
        try {
            sendEmail(smsScrapOrder.getScrapNo(),mailUsers);
        } catch (Exception e) {
            log.error("报废审批发送邮件失败!{}", e);
        }
        return R.ok("提交成功！");
    }

    /**
     * 开启流程 报废申请单逻辑(列表提交)
     * 待加全局事务
     *
     * @param smsScrapOrder
     * @return R
     */
    @Override
    @GlobalTransactional
    public R startActOnlyForList(SmsScrapOrder smsScrapOrder, long userId) {
        log.info(StrUtil.format("报废申请开启流程（列表）：参数为{}", smsScrapOrder.toString()));
        //判断状态是否是未提交，如果不是则抛出错误
        Long id = smsScrapOrder.getId();
        if (id == null) {
            return R.error("id不能为空！");
        }
        SmsScrapOrder smsScrapOrderCheck = remoteSmsScrapOrderService.get(id);
        if (smsScrapOrder == null) {
            log.error(StrUtil.format("(报废)未找到此报废数据：id{}", id));
            return R.error("未找到报废数据！");
        }
        if (!ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode().equals(smsScrapOrderCheck.getScrapStatus())) {
            log.error(StrUtil.format("(报废)只有待提交状态数据可以提交：{}", smsScrapOrderCheck.getScrapStatus()));
            return R.error("只有待提交状态数据可以提交！");
        }
        //更新数据
        smsScrapOrder.setSubmitDate(DateUtil.date());
        Preconditions.checkArgument(StrUtil.isNotEmpty(smsScrapOrder.getIsPay()),"是否买单为空，不允许提交！");
        if (IsPayEnum.IS_PAY_NO.getCode().equals(smsScrapOrder.getIsPay())) {
            //不买单
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLSH.getCode());
        }else{
            //买单
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSH.getCode());
        }
        R rUpdate = remoteSmsScrapOrderService.update(smsScrapOrder);
        if (!rUpdate.isSuccess()) {
            throw new BusinessException(rUpdate.getStr("msg"));
        }
        smsScrapOrder.setScrapNo(smsScrapOrderCheck.getScrapNo());
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsScrapOrder, userId);
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //指定下一审批人
        Set<String> userIds = new HashSet<>();
        List<SysUser> mailUsers = new ArrayList<>();
        R omsProductionOrderResult = remoteProductionOrderService.selectByProdctOrderCode(smsScrapOrderCheck.getProductOrderCode());
        if (!omsProductionOrderResult.isSuccess()) {
            throw new BusinessException("订单信息不存在");
        }
        OmsProductionOrder omsProductionOrder = omsProductionOrderResult.getData(OmsProductionOrder.class);
        smsScrapOrder.setFactoryCode(omsProductionOrder.getProductFactoryCode());
        if (IsPayEnum.IS_PAY_NO.getCode().equals(smsScrapOrder.getIsPay())) {
            //不买单  各工厂质量经理审核
            R rUser = remoteUserService.selectUserByMaterialCodeAndRoleKey(smsScrapOrder.getFactoryCode()
                    ,  RoleConstants.ROLE_KEY_ZLGCS);
            if (!rUser.isSuccess()) {
                log.error("不买单报废提交失败，下一级审核人为空！");
                throw new BusinessException("不买单报废提交失败，下一级审核人为空！");
            }
            List<SysUserVo> users = rUser.getCollectData(new TypeReference<List<SysUserVo>>() {});
            userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.toSet());
            mailUsers = users.stream().map(user -> {
                SysUser u = SysUser.builder().email(user.getEmail()).userName(user.getUserName()).build();
                return u;
            }).collect(Collectors.toList());
        }else{
            //买单 由生产订单创建人审批

            String createBy = omsProductionOrder.getCreateBy();
            SysUser createUser=remoteUserService.selectSysUserByUsername(createBy);
            userIds = CollectionUtil.set(false, createUser.getUserId().toString());
            mailUsers.add(createUser);
        }
        bizBusinessService.startProcess(business, variables,userIds);
        //发送邮件通知
        try {
            sendEmail(smsScrapOrder.getScrapNo(),mailUsers);
        } catch (Exception e) {
            log.error("报废审批发送邮件失败!{}", e);
        }
        return R.ok("提交成功！");
    }

    /**
     * 买单报废审批流程 报废申请单逻辑
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
    public R auditLogic(BizAudit bizAudit, long userId) {
        log.info(StrUtil.format("报废申请审核：参数为{}", bizAudit.toString()));
        Set<String> userIds = new HashSet<>();
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            log.error(StrUtil.format("(报废)流程业务表数据为空：id{}", bizAudit.getBusinessKey()));
            return R.error("流程业务表数据为空！");
        }
        //查询物耗表信息
        SmsScrapOrder smsScrapOrder = remoteSmsScrapOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (smsScrapOrder == null) {
            log.error(StrUtil.format("(报废)未找到此报废数据：id{}", bizBusiness.getTableId()));
            return R.error("未找到此业务数据！");
        }
        //审批结果
        Boolean result = false;
        if (bizAudit.getResult().intValue() == 2) {
            result = true;
        }
        //判断下级审批  修改状态
        if (result) {
            //审批通过
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode());
            }else if(ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSH.getCode().equals(smsScrapOrder.getScrapStatus())){
                //排产员审核通过至业务科审核
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
                //审批 推进工作流
                //指定下一审批人
                R rUser = remoteUserService.selectUserByMaterialCodeAndRoleKey(smsScrapOrder.getFactoryCode()
                        ,  RoleConstants.ROLE_KEY_YWK);
                if (!rUser.isSuccess()) {
                    log.error("报废审批失败，下一级审核人为空！");
                    throw new BusinessException("报废审批失败，下一级审核人为空！");
                }
                List<SysUserVo> users = rUser.getCollectData(new TypeReference<List<SysUserVo>>() {
                });
                //发送邮件通知
                try {
                    sendEmail(smsScrapOrder.getScrapNo(),users.stream().map(o-> {
                        SysUser sysUser = new SysUser();
                        sysUser.setEmail(o.getEmail());
                        return sysUser;
                    }).collect(Collectors.toList()));
                } catch (Exception e) {
                    log.error("报废发送邮件失败!{}", e);
                }
                userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.toSet());
            }else {
                log.error(StrUtil.format("(报废)此状态数据不允许审核：{}", smsScrapOrder.getScrapStatus()));
                return R.error("此状态数据不允许审核！");
            }
        } else {
            //审批驳回
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBH.getCode());
            }else if(ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYBH.getCode());
            } else {
                log.error(StrUtil.format("(报废)此状态数据不允许审核：{}", smsScrapOrder.getScrapStatus()));
                return R.error("此状态数据不允许审核！");
            }
        }
        if (ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode().equals(smsScrapOrder.getScrapStatus())) {
            //业务科审核通过传SAP
            R r = remoteSmsScrapOrderService.autidSuccessToSAP261(smsScrapOrder);
            if (!r.isSuccess()) {
                throw new BusinessException(r.getStr("msg"));
            }
            Console.log("===========执行传SAP===========================");
        }
        R r = remoteSmsScrapOrderService.update(smsScrapOrder);
        if (r.isSuccess()) {
            //审批 推进工作流
            R rResult = new R();
            rResult.put("bizAudit",bizAudit);
            rResult.put("userId",userId);
            rResult.put("userIds",userIds);
            return rResult;
        }else{
            throw new BusinessException(r.getStr("msg"));
        }
    }

    /**
     * 不买单报废审批流程 报废申请单逻辑
     *
     * @param bizAudit
     * @param userId
     * @return R
     */
    @Override
    @GlobalTransactional
    public R auditNoPay(BizAudit bizAudit, long userId) {
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
    public R auditNoPayLogic(BizAudit bizAudit, long userId) {
        log.info(StrUtil.format("报废不买单申请审核：参数为{}", bizAudit.toString()));
        Set<String> userIds = new HashSet<>();
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            log.error(StrUtil.format("(不买单报废)流程业务表数据为空：id{}", bizAudit.getBusinessKey()));
            return R.error("流程业务表数据为空！");
        }
        //查询物耗表信息
        SmsScrapOrder smsScrapOrder = remoteSmsScrapOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (smsScrapOrder == null) {
            log.error(StrUtil.format("(不买单报废)未找到此报废数据：id{}", bizBusiness.getTableId()));
            return R.error("未找到此业务数据！");
        }
        //审批结果
        Boolean result = false;
        if (bizAudit.getResult().intValue() == 2) {
            result = true;
        }
        //判断下级审批  修改状态
        if (result) {
            //需要发送邮件的用户
            List<SysUserVo> users = new ArrayList<>();
            String roleKey = new String();//下一级审批角色
            //审批通过
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSHBMD.getCode().equals(smsScrapOrder.getScrapStatus())) {
                //业务科审核通过
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_WXJS.getCode());
                smsScrapOrder.setSettleFee(BigDecimal.ZERO);
            }else if(ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLSH.getCode().equals(smsScrapOrder.getScrapStatus())){
                //质量工程师审批至订单经理
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DDJLSH.getCode());
                roleKey = RoleConstants.ROLE_KEY_ORDER;
            }else if(ScrapOrderStatusEnum.BF_ORDER_STATUS_DDJLSH.getCode().equals(smsScrapOrder.getScrapStatus())){
                //订单经理审核通过至业务科审核
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSHBMD.getCode());
                roleKey = RoleConstants.ROLE_KEY_YWK;
            }else {
                log.error(StrUtil.format("(报废)此状态数据不允许审核：{}", smsScrapOrder.getScrapStatus()));
                return R.error("此状态数据不允许审核！");
            }
            //指定下一审批人
            if (StrUtil.isNotEmpty(roleKey)) {
                R rUser = remoteUserService.selectUserByMaterialCodeAndRoleKey(smsScrapOrder.getFactoryCode()
                        ,  roleKey);
                if (!rUser.isSuccess()) {
                    log.error("报废审批失败，下一级审核人为空！");
                    throw new BusinessException("报废审批失败，下一级审核人为空！");
                }
                users = rUser.getCollectData(new TypeReference<List<SysUserVo>>() {
                });
                userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.toSet());
                //发送邮件通知
                try {
                    sendEmail(smsScrapOrder.getScrapNo(),users.stream().map(o-> {
                        SysUser sysUser = new SysUser();
                        sysUser.setEmail(o.getEmail());
                        return sysUser;
                    }).collect(Collectors.toList()));
                } catch (Exception e) {
                    log.error("报废发送邮件失败!{}", e);
                }
            }
        } else {
            //审批驳回
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLBH.getCode());
            }else if(ScrapOrderStatusEnum.BF_ORDER_STATUS_DDJLSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DDJLBH.getCode());
            } else if(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSHBMD.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBHBMD.getCode());
            }else {
                log.error(StrUtil.format("(不买单报废)此状态数据不允许审核：{}", smsScrapOrder.getScrapStatus()));
                return R.error("此状态数据不允许审核！");
            }
        }
        if (ScrapOrderStatusEnum.BF_ORDER_STATUS_WXJS.getCode().equals(smsScrapOrder.getScrapStatus())) {
            //业务科审核通过传SAP
            R r = remoteSmsScrapOrderService.autidSuccessToSAP261(smsScrapOrder);
            if (!r.isSuccess()) {
                throw new BusinessException(r.getStr("msg"));
            }
            Console.log("===========执行传SAP===========================");
        }
        R r = remoteSmsScrapOrderService.update(smsScrapOrder);
        if (r.isSuccess()) {
            //审批 推进工作流
            R rResult = new R();
            rResult.put("bizAudit",bizAudit);
            rResult.put("userId",userId);
            rResult.put("userIds",userIds);
            return rResult;
        }else{
            throw new BusinessException(r.getStr("msg"));
        }
    }

    /**
     * 发送邮件
     * @param scrapNo
     * @param sysUserVoList
     */
    private void sendEmail(String scrapNo, List<SysUser> sysUserVoList) {
        //校验邮件
        for(SysUser sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            if(StringUtils.isBlank(email)){
                throw new  BusinessException("用户"+sysUserVo.getUserName()+"邮箱不存在");
            }
        }
        //发送邮件
        for(SysUser sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            String subject = "报废申请单审批";
            String content = "您有一条待办消息要处理!" + "报废申请单 单号:" + scrapNo +  EmailConstants.ORW_URL;
            mailService.sendTextMail(email,subject,content);
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
            SmsScrapOrder smsScrapOrder = remoteSmsScrapOrderService.get(Long.valueOf(business.getTableId()));
            R result = new R();
            result.put("procInstId",business.getProcInstId());
            result.put("data", smsScrapOrder);
            return result;
        }
        return R.error("no record");
    }

    /**
     * biz构造业务信息
     *
     * @param smsScrapOrder
     * @return
     * @author cs
     */
    private BizBusiness initBusiness(SmsScrapOrder smsScrapOrder, long userId) {
        BizBusiness business = new BizBusiness();
        String proKey = new String();
        String titile = new String();
        //获取流程信息
        if (IsPayEnum.IS_PAY_NO.getCode().equals(smsScrapOrder.getIsPay())) {
            //不买单流程
            proKey = ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_NO_PAY_SCRAP;
            titile = ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SCRAP_NO_PAY_TEST;
        }else{
            //买单
            proKey = ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_SCRAP_TEST;
            titile = ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SCRAP_TEST;
        }
        R keyMap = actTaskService.getByKey(proKey);
        if (!keyMap.isSuccess()) {
            log.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        business.setProcDefId(processDefinitionAct.getId());
        business.setProcName(processDefinitionAct.getName());
        business.setOrderNo(smsScrapOrder.getScrapNo());
        business.setTableId(smsScrapOrder.getId().toString());
        business.setTableName(ActivitiTableNameConstants.ACTIVITI_TABLE_NAME_SCRAP);
        business.setTitle(titile);
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}
