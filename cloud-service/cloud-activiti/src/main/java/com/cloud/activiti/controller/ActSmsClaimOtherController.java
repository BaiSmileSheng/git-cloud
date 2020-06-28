package com.cloud.activiti.controller;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.service.IActSmsClaimOtherService;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 其他索赔审核工作流
 * @Author Lihongxia
 * @Date 2020-05-29
 */
@RestController
@RequestMapping("actSmsClaimOther")
@Api(tags = "其他索赔审核工作流")
public class ActSmsClaimOtherController extends BaseController {

    @Autowired
    private IActSmsClaimOtherService actSmsClaimOtherService;


    /**
     * 根据业务key获取其他索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 其他索赔信息
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取其他索赔信息",response = SmsQualityOrder.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        return actSmsClaimOtherService.getBizInfoByTableId(businessKey);
    }

    /**
     * 其他索赔开启流程

     * @return 成功或失败
     */
    @HasPermissions("activiti:actSmsClaimOther:save")
    @PostMapping("save")
    @ApiOperation(value = "供应商申诉时开启其他索赔流程",response = SmsQualityOrder.class)
    public R addSave(@RequestParam("id") Long id,@RequestParam("complaintDescription")String complaintDescription, @RequestPart("files") MultipartFile[] files) {
        //获取当前用户登录信息
        SysUser sysUser = getUserInfo(SysUser.class);
        return actSmsClaimOtherService.addSave(id,complaintDescription,files,sysUser);
    }

    /**
     * 其他索赔流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @HasPermissions("activiti:actSmsClaimOther:audit")
    @PostMapping("audit")
    @ApiOperation(value = "索赔流程审批",response = SmsQualityOrder.class)
    public R audit(@RequestBody BizAudit bizAudit) {
        //获取当前用户登录信息
        SysUser sysUser = getUserInfo(SysUser.class);
        //判断当前用户是否有权限操作
        if(!sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ) ){
            return R.error("没有权限进行审批");
        }
        return actSmsClaimOtherService.audit(bizAudit,sysUser);
    }
}
