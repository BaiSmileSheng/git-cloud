package com.cloud.activiti.controller;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.service.IActSmsSupplementaryOrderService;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物耗申请单 审核流程
 *
 * @author cs
 * @date 2020-05-20
 */
@RestController
@RequestMapping("actSmsSupplementaryOrder")
@Api(tags = "物耗申请单审核流程 ")
public class ActSmsSupplementaryOrderActController extends BaseController {
    @Autowired
    private IActSmsSupplementaryOrderService actSmsSupplementaryOrderService;

    /**
     * 根据业务key获取数据
     *
     * @param businessKey
     * @return smsSupplementaryOrder
     * @author cs
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取数据", response = SmsSupplementaryOrder.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        return actSmsSupplementaryOrderService.getBizInfoByTableId(businessKey);
    }

    /**
     * 物耗审核开启流程  新增、编辑提交时开启
     * @param smsSupplementaryOrders
     * @param procDefId
     * @param procName
     * @return
     */
    @PostMapping("open")
    @OperLog(title = "物耗审核开启流程(新增、编辑)", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "开启流程(新增、编辑) ", response = R.class)
    @HasPermissions("settle:supplementary:commit")
    public R addSave(@RequestBody List<SmsSupplementaryOrder> smsSupplementaryOrders, String procDefId, String procName) {
        //开启审核流程
        return actSmsSupplementaryOrderService.startActList(smsSupplementaryOrders,getUserInfo(SysUser.class),procDefId,procName);
    }

    /**
     * 物耗审核开启流程  列表提交时开启
     *
     * @param smsSupplementaryOrder
     * @return R 成功/失败
     */
    @PostMapping("openOnlyForList")
    @OperLog(title = "物耗审核开启流程(列表提交)", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "开启流程(列表提交) ", response = R.class)
    @HasPermissions("settle:supplementary:commit")
    public R addSaveOnlyForList(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder) {
        //开启审核流程
        return actSmsSupplementaryOrderService.startActOnlyForList(smsSupplementaryOrder,getCurrentUserId());
    }

    /**
     * 物耗流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @PostMapping("audit")
    @ApiOperation(value = "物耗流程审批 ", response = R.class)
    @HasPermissions("settle:supplementary:audit")
    public R audit(@RequestBody BizAudit bizAudit) {
        return actSmsSupplementaryOrderService.audit(bizAudit,getCurrentUserId());
    }
}
