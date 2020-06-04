package com.cloud.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsSupplementaryOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ActivitiProTitleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsSupplementaryOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service
public class ActSmsSupplementaryOrderServiceImpl implements IActSmsSupplementaryOrderService {
    @Autowired
    private IBizBusinessService bizBusinessService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private RemoteSmsSupplementaryOrderService remoteSmsSupplementaryOrderService;
    @Autowired
    private IActTaskService actTaskService;


    /**
     * 开启流程 物耗申请单逻辑  新增、编辑提交时开启
     * 待加全局事务
     *
     * @param smsSupplementaryOrder
     * @return R
     */
    @Override
//    @GlobalTransactional
    public R startAct(SmsSupplementaryOrder smsSupplementaryOrder, SysUser sysUser) {
        if (smsSupplementaryOrder.getId() == null) {
            //新增提交  校验  获取数据  插入数据  开启流程
            smsSupplementaryOrder.setCreateBy(sysUser.getLoginName());
            R rAdd = remoteSmsSupplementaryOrderService.addSave(smsSupplementaryOrder);
            if (!rAdd.isSuccess()) {
                return rAdd;
            }
            Long id = Long.valueOf(rAdd.get("data").toString());
            smsSupplementaryOrder.setId(id);
        } else {
            //修改提交  校验  更新数据  开启流程
            smsSupplementaryOrder.setSubmitDate(DateUtil.date());
            smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode());
            R rUpdate = remoteSmsSupplementaryOrderService.editSave(smsSupplementaryOrder);
            if (!rUpdate.isSuccess()) {
                return rUpdate;
            }
        }
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsSupplementaryOrder, sysUser.getUserId());
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(business, variables);
        return R.ok("提交成功！");
    }

    /**
     * 开启流程 物耗申请单逻辑  列表提交时开启
     *
     * @param smsSupplementaryOrder
     * @param userId
     * @return
     */
    @Override
//    @GlobalTransactional
    public R startActOnlyForList(SmsSupplementaryOrder smsSupplementaryOrder, long userId) {
        //列表提交  更新数据  开启流程
        Long id = smsSupplementaryOrder.getId();
        if (id == null) {
            return R.error("id不能为空！");
        }
        SmsSupplementaryOrder smsSupplementaryOrderCheck = remoteSmsSupplementaryOrderService.get(id);
        if (!SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode().equals(smsSupplementaryOrderCheck.getStuffStatus())) {
            return R.error("只有待提交状态数据可以提交！");
        }
        //更新数据
        smsSupplementaryOrder.setSubmitDate(DateUtil.date());
        smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode());
        R rUpdate = remoteSmsSupplementaryOrderService.update(smsSupplementaryOrder);
        if (!rUpdate.isSuccess()) {
            return rUpdate;
        }
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsSupplementaryOrder, userId);
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(business, variables);
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
//    @GlobalTransactional
    public R audit(BizAudit bizAudit, long userId) {
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            return R.error("流程业务表数据为空！");
        }
        //查询物耗表信息
        SmsSupplementaryOrder smsSupplementaryOrder = remoteSmsSupplementaryOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (smsSupplementaryOrder == null) {
            return R.error("未找到此业务数据！");
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
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode());
            } else if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                //TODO:小微主审核通过   传SAP

            } else {
                throw new BusinessException("状态错误！");
            }
        } else {
            //审批驳回
            if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITBH.getCode());
            } else if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZBH.getCode());
            } else {
                throw new BusinessException("状态错误！");
            }
        }
        R r = remoteSmsSupplementaryOrderService.update(smsSupplementaryOrder);
        if (r.isSuccess()) {
            //审批 推进工作流
            return actTaskService.audit(bizAudit, userId);
        }
        return R.error();
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
            return R.data(smsSupplementaryOrder);
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
        business.setTableId(smsSupplementaryOrder.getId().toString());
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
