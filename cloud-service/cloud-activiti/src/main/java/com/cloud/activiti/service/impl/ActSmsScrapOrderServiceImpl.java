package com.cloud.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsScrapOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ActivitiProTitleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsScrapOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service
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
     * 开启流程 报废申请单逻辑
     * 待加全局事务
     * @param smsScrapOrder
     * @return R
     */
    @Override
    public R startAct(SmsScrapOrder smsScrapOrder,long userId) {
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsScrapOrder,userId);
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(business, variables);

        //修改状态 jit待审核
        smsScrapOrder.setSubmitDate(DateUtil.date());
        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
        return remoteSmsScrapOrderService.update(smsScrapOrder);
    }

    /**
     * 审批流程 报废申请单逻辑
     * 待加全局事务
     * @param bizAudit
     * @param smsScrapOrder
     * @param userId
     * @return R
     */
    @Override
    public R audit(BizAudit bizAudit, SmsScrapOrder smsScrapOrder, long userId) {
        //审批结果
        Boolean result = false;
        if(bizAudit.getResult().intValue()==2){
            result = true;
        }
        //判断下级审批  修改状态
        if (result) {
            //审批通过
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                //TODO:业务科审核通过  传SAP

            } else {
                throw new BusinessException("状态错误！");
            }
        }else{
            //审批驳回
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBH.getCode());
            }  else {
                throw new BusinessException("状态错误！");
            }
        }
        R r=remoteSmsScrapOrderService.update(smsScrapOrder);
        if(r.isSuccess()){
            //审批 推进工作流
            return actTaskService.audit(bizAudit, userId);
        }
        return R.error();
    }

    /**
     * biz构造业务信息
     *
     * @param smsScrapOrder
     * @return
     * @author cs
     */
    private BizBusiness initBusiness(SmsScrapOrder smsScrapOrder,long userId) {
        BizBusiness business = new BizBusiness();
        business.setTableId(smsScrapOrder.getId().toString());
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
