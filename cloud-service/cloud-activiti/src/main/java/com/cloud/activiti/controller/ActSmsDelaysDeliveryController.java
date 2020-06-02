package com.cloud.activiti.controller;

import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsDelaysDeliveryService;
import com.cloud.activiti.service.IActSmsQualityOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ActivitiProTitleConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.enums.DeplayStatusEnum;
import com.cloud.settle.feign.RemoteDelaysDeliveryService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("actSmsDelaysDelivery")
public class ActSmsDelaysDeliveryController extends BaseController {

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private IActSmsDelaysDeliveryService actSmsDelaysDeliveryService;

    /**
     * 根据业务key获取延期索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取延期索赔信息",response = SmsDelaysDelivery.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        return actSmsDelaysDeliveryService.getBizInfoByTableId(businessKey);
    }

    /**
     * 开启延期索赔流程
     * @param smsDelaysDelivery 延期索赔信息
     * @return 成功或失败
     */
    @PostMapping("save")
    @ApiOperation(value = "开启延期索赔流程",response = SmsDelaysDelivery.class)
    public R addSave(@RequestBody SmsDelaysDelivery smsDelaysDelivery) {
        //获取当前用户登录信息
        SysUser sysUser = getUserInfo(SysUser.class);
        return actSmsDelaysDeliveryService.addSave(smsDelaysDelivery,sysUser);
    }

    /**
     * 延期索赔流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @PostMapping("audit")
    @ApiOperation(value = "延期索赔流程审批",response = SmsDelaysDelivery.class)
    public R audit(@RequestBody BizAudit bizAudit) {
        //获取当前用户登录信息
        SysUser sysUser = getUserInfo(SysUser.class);
        return actSmsDelaysDeliveryService.audit(bizAudit,sysUser);
    }
}
