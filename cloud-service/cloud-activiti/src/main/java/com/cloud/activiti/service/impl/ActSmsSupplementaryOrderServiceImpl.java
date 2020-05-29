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
     * 开启流程 物耗申请单逻辑
     *
     * @param smsSupplementaryOrder
     * @return R
     */
    @Override
    public R startAct(SmsSupplementaryOrder smsSupplementaryOrder,long userId) {
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsSupplementaryOrder,userId);
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(business, variables);

        //修改状态 jit待审核
        smsSupplementaryOrder.setSubmitDate(DateUtil.date());
        smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode());
        return remoteSmsSupplementaryOrderService.update(smsSupplementaryOrder);
    }

    /**
     * 审批流程 物耗申请单逻辑
     * @param bizAudit
     * @param smsSupplementaryOrder
     * @param userId
     * @return R
     */
    @Override
    public R audit(BizAudit bizAudit, SmsSupplementaryOrder smsSupplementaryOrder, long userId) {
        //审批结果
        Boolean result = false;
        if(bizAudit.getResult().intValue()==2){
            result = true;
        }
        //判断下级审批  修改状态
        //1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、
        if (result) {
            //审批通过
            if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode());
            } else if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZSHTG.getCode());
            } else {
                throw new BusinessException("状态错误！");
            }
        }else{
            //审批驳回
            if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JITBH.getCode());
            } else if (SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZDSH.getCode().equals(smsSupplementaryOrder.getStuffStatus())) {
                smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_XWZBH.getCode());
            } else {
                throw new BusinessException("状态错误！");
            }
        }
        R r=remoteSmsSupplementaryOrderService.update(smsSupplementaryOrder);
        if(r.isSuccess()){
            //审批 推进工作流
            return actTaskService.audit(bizAudit, userId);
        }
        return R.error();
    }

    /**
     * biz构造业务信息
     *
     * @param smsSupplementaryOrder
     * @return
     * @author cs
     */
    private BizBusiness initBusiness(SmsSupplementaryOrder smsSupplementaryOrder,long userId) {
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
