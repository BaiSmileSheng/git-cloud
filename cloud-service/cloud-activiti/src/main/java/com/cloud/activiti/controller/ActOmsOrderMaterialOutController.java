package com.cloud.activiti.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.activiti.service.IActOmsOrderMaterialOutService;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsRealOrder;
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

import java.util.List;

/**
 * 下市审批工作流
 * @Author Lihongxia
 * @Date 2020-06-22
 */
@RestController
@RequestMapping("actOmsOrderMaterialOut")
@Api(tags = "下市审批工作流")
public class ActOmsOrderMaterialOutController extends BaseController {

    @Autowired
    private IActOmsOrderMaterialOutService actOmsOrderMaterialOutService;


    /**
     * 根据业务key获取下市审核信息
     * @param businessKey biz_business的主键
     * @return 查询结果
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取下市审核信息",response = OmsRealOrder.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        return actOmsOrderMaterialOutService.getBizInfoByTableId(businessKey);
    }

    /**
     * 下市审核开启流程
     * @return 成功或失败
     */
    @PostMapping("save")
    @ApiOperation(value = "导入时物料下市开启真单审批流程",response = OmsRealOrder.class)
    public R addSave(@RequestBody OmsOrderMaterialOutVo omsOrderMaterialOutVo) {
        return actOmsOrderMaterialOutService.addSave(omsOrderMaterialOutVo);
    }

    /**
     * 下市流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @HasPermissions("activiti:actOmsRealOrder:audit")
    @PostMapping("audit")
    @ApiOperation(value = "下市流程审批",response = SmsQualityOrder.class)
    public R audit(@RequestBody BizAudit bizAudit) {
        //获取当前用户登录信息
        SysUser sysUser = getUserInfo(SysUser.class);
        return actOmsOrderMaterialOutService.audit(bizAudit,sysUser);
    }
}
