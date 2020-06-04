package com.cloud.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsScrapOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.activiti.consts.ActivitiProTitleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsScrapOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
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
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;


    /**
     * 开启流程 报废申请单逻辑(编辑、新增提交)
     * 待加全局事务
     *
     * @param smsScrapOrder
     * @return R
     */
    @Override
//    @GlobalTransactional
    public R startAct(SmsScrapOrder smsScrapOrder, long userId) {
        //判断状态是否是未提交，如果不是则抛出错误
        if (smsScrapOrder.getId() == null) {
            R rAdd = remoteSmsScrapOrderService.addSave(smsScrapOrder);
            if (!rAdd.isSuccess()) {
                return rAdd;
            }
            Long id = (Long) rAdd.get("data");
            smsScrapOrder.setId(id);
        } else {
            //编辑提交  更新数据
            smsScrapOrder.setSubmitDate(DateUtil.date());
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
            R rUpdate = remoteSmsScrapOrderService.editSave(smsScrapOrder);
            if (!rUpdate.isSuccess()) {
                return rUpdate;
            }
        }
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsScrapOrder, userId);
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
//    @GlobalTransactional
    public R startActOnlyForList(SmsScrapOrder smsScrapOrder, long userId) {
        //判断状态是否是未提交，如果不是则抛出错误
        Long id = smsScrapOrder.getId();
        if (id != null) {
            return R.error("id不能为空！");
        }
        SmsScrapOrder smsScrapOrderCheck = remoteSmsScrapOrderService.get(id);
        if (!ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode().equals(smsScrapOrder.getScrapStatus())) {
            return R.error("只有待提交状态数据可以提交！");
        }
        //更新数据
        smsScrapOrder.setSubmitDate(DateUtil.date());
        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode());
        R rUpdate = remoteSmsScrapOrderService.update(smsScrapOrder);
        if (!rUpdate.isSuccess()) {
            return rUpdate;
        }
        //插入流程物业表  并开启流程
        BizBusiness business = initBusiness(smsScrapOrder, userId);
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
//    @GlobalTransactional
    public R audit(BizAudit bizAudit, long userId) {
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            return R.error("流程业务表数据为空！");
        }
        //查询物耗表信息
        SmsScrapOrder smsScrapOrder = remoteSmsScrapOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (smsScrapOrder == null) {
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
                //TODO:业务科审核通过  传SAP

            } else {
                return R.error("状态错误！");
            }
        } else {
            //审批驳回
            if (ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode().equals(smsScrapOrder.getScrapStatus())) {
                smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBH.getCode());
            } else {
                return R.error("状态错误！");
            }
        }
        R r = remoteSmsScrapOrderService.update(smsScrapOrder);
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
