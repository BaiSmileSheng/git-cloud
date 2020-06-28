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
import com.cloud.activiti.service.IActSmsScrapOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsScrapOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


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
        //判断状态是否是未提交，如果不是则抛出错误
        if (smsScrapOrder.getId() == null) {
            smsScrapOrder.setCreateBy(sysUser.getLoginName());
            smsScrapOrder.setSubmitDate(DateUtil.date());
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
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
            smsScrapOrder.setSubmitDate(DateUtil.date());
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
            R rUpdate = remoteSmsScrapOrderService.editSave(smsScrapOrder);
            if (!rUpdate.isSuccess()) {
                log.info("报废申请更新结果：{}", rUpdate.toString());
                throw new BusinessException(rUpdate.getStr("msg"));
            }
        }
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsScrapOrder, sysUser.getUserId());
        //获取流程信息
        R keyMap = actTaskService.getByKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_SCRAP_TEST);
        if (!keyMap.isSuccess()) {
            log.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        business.setProcDefId(processDefinitionAct.getId());
        business.setProcName(processDefinitionAct.getName());
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(business, variables);
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
        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
        R rUpdate = remoteSmsScrapOrderService.update(smsScrapOrder);
        if (!rUpdate.isSuccess()) {
            throw new BusinessException(rUpdate.getStr("msg"));
        }
        smsScrapOrder.setScrapNo(smsScrapOrderCheck.getScrapNo());
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsScrapOrder, userId);
        R keyMap = actTaskService.getByKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_SCRAP_TEST);
        if (!keyMap.isSuccess()) {
            log.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        business.setProcDefId(processDefinitionAct.getId());
        business.setProcName(processDefinitionAct.getName());
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(business, variables);
        return R.ok("提交成功！");
    }

    /**
     * 审批流程 报废申请单逻辑
     * 待加全局事务
     *
     * @param bizAudit
     * @param userId
     * @return R
     */
    @Override
    @GlobalTransactional
    public R audit(BizAudit bizAudit, long userId) {
        log.info(StrUtil.format("报废申请审核：参数为{}", bizAudit.toString()));
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
                //业务科审核通过传SAP
                R r = remoteSmsScrapOrderService.autidSuccessToSAP261(smsScrapOrder);
                if (!r.isSuccess()) {
                    throw new BusinessException(r.getStr("msg"));
                }
            } else {
                log.error(StrUtil.format("(报废)此状态数据不允许审核：{}", smsScrapOrder.getScrapStatus()));
                return R.error("此状态数据不允许审核！");
            }
        } else {
            //审批驳回
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBH.getCode());
                R r = remoteSmsScrapOrderService.update(smsScrapOrder);
                if (!r.isSuccess()) {
                    throw new BusinessException(r.getStr("msg"));
                }
            } else {
                log.error(StrUtil.format("(报废)此状态数据不允许审核：{}", smsScrapOrder.getScrapStatus()));
                return R.error("此状态数据不允许审核！");
            }
        }
        //审批 推进工作流
        return actTaskService.audit(bizAudit, userId);
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
            return R.data(smsScrapOrder);
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
        business.setOrderNo(smsScrapOrder.getScrapNo());
        business.setTableId(smsScrapOrder.getId().toString());
        business.setTableName(ActivitiTableNameConstants.ACTIVITI_TABLE_NAME_SCRAP);
        business.setProcDefId(smsScrapOrder.getProcDefId());
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SCRAP_TEST);
        business.setProcName(smsScrapOrder.getProcName());
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}
