package com.cloud.activiti.controller;

import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ActivitiProTitleConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.feign.RemoteQualityOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * 质量索赔审核工作流
 * @Author Lihongxia
 * @Date 2020-05-29
 */
@RestController
@RequestMapping("actSmsQuality")
public class ActSmsQualityOrderController extends BaseController {

    @Autowired
    private IBizBusinessService bizBusinessService;

    @Autowired
    private RemoteQualityOrderService remoteQualityOrderService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private IActTaskService actTaskService;


    /**
     * 根据业务key获取质量索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    @GetMapping("biz/{businessKey}")
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            SmsQualityOrder smsQualityOrder  = remoteQualityOrderService.get(Long.valueOf(business.getTableId()));
            return R.data(smsQualityOrder);
        }
        return R.error("no record");
    }

    /**
     * 开启流程
     * @param smsQualityOrder
     * @return
     */
    @PostMapping("save")
    public R addSave(@RequestBody SmsQualityOrder smsQualityOrder) {

        BizBusiness business = initBusiness(smsQualityOrder);
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        bizBusinessService.startProcess(business, variables);
        return R.ok();
    }

    /**
     * biz构造业务信息
     * @param smsQualityOrder
     * @return
     */
    private BizBusiness initBusiness(SmsQualityOrder smsQualityOrder) {
        BizBusiness business = new BizBusiness();
        business.setTableId(smsQualityOrder.getId().toString());
        business.setProcDefId(smsQualityOrder.getProcDefId());
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SQUALITY_TEST);
        business.setProcName(smsQualityOrder.getProcName());
        long userId = getCurrentUserId();
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @PostMapping("audit")
    public R audit(@RequestBody BizAudit bizAudit) {
        //可处理业务逻辑
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            return R.error();
        }
        SmsQualityOrder smsQualityOrder = remoteQualityOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        Boolean flagStatus4 = QualityStatusEnum.QUALITY_STATUS_4.getCode().equals(smsQualityOrder.getQualityStatus());
        Boolean flagStatus5 = QualityStatusEnum.QUALITY_STATUS_5.getCode().equals(smsQualityOrder.getQualityStatus());
        if (null == smsQualityOrder || !flagStatus4 || !flagStatus5) {
            return R.error();
        }
        smsQualityOrder.setRemark("我已经审批了，审批结果：" + bizAudit.getResult());
        //根据角色...设置状态   质量部长审核后5小微主待审核   小微主审核后 6小微主审核通过
        SysUser sysUser = getUserInfo(SysUser.class);
        if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_ZLBBZ)){
            if(flagStatus4){
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_5.getCode());
            }
        }
        if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)){
            if(flagStatus5){
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_11.getCode());
            }
        }
        R updateResult = remoteQualityOrderService.editSave(smsQualityOrder);
        if("0".equals(updateResult.get("code").toString())){
            //审批 推进工作流
            return actTaskService.audit(bizAudit, getCurrentUserId());
        }
        return R.error();
    }
}
