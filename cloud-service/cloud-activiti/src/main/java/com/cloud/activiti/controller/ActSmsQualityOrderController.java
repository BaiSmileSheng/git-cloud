package com.cloud.activiti.controller;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.service.IActSmsQualityOrderService;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 质量索赔审核工作流
 * @Author Lihongxia
 * @Date 2020-05-29
 */
@RestController
@RequestMapping("actSmsQuality")
@Api(tags = "质量索赔审核工作流")
public class ActSmsQualityOrderController extends BaseController {

    @Autowired
    private IActSmsQualityOrderService actSmsQualityOrderService;


    /**
     * 根据业务key获取质量索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取质量索赔信息",response = SmsQualityOrder.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        return actSmsQualityOrderService.getBizInfoByTableId(businessKey);
    }

    /**
     * 供应商申诉时质量索赔开启流程
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param ossIds
     * @return 成功或失败
     */
    @HasPermissions("activiti:actSmsQuality:save")
    @PostMapping("save")
    @ApiOperation(value = "供应商申诉时质量索赔开启流程",response = SmsQualityOrder.class)
    @OperLog(title = "供应商申诉时质量索赔开启流程", businessType = BusinessType.UPDATE)
    public R addSave(@RequestParam("id") Long id, @RequestParam("complaintDescription") String complaintDescription,
                     @RequestParam("ossIds") String ossIds) {
        if(null == id){
            return R.error("id不存在");
        }
        if(StringUtils.isBlank(complaintDescription)){
            return R.error("请填写申诉描述");
        }
        if(StringUtils.isBlank(ossIds)){
            return R.error("请上传图片");
        }
        //获取当前用户登录信息
        SysUser sysUser = getUserInfo(SysUser.class);
        return actSmsQualityOrderService.addSave(id,complaintDescription,ossIds,sysUser);
    }

    /**
     * 流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @HasPermissions("activiti:actSmsQuality:audit")
    @PostMapping("audit")
    @ApiOperation(value = "索赔流程审批",response = SmsQualityOrder.class)
    @OperLog(title = "质量索赔流程审批", businessType = BusinessType.UPDATE)
    public R audit(@RequestBody BizAudit bizAudit) {
        //获取当前用户登录信息
        SysUser sysUser = getUserInfo(SysUser.class);
        return actSmsQualityOrderService.audit(bizAudit,sysUser);
    }
}
