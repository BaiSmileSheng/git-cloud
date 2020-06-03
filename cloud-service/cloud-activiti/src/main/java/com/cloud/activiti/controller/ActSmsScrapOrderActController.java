package com.cloud.activiti.controller;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.service.IActSmsScrapOrderService;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 报废申请单 审核流程
 *
 * @author cs
 * @date 2020-05-20
 */
@RestController
@RequestMapping("actScrapOrder")
@Api(tags = "报废申请单审核流程 ")
public class ActSmsScrapOrderActController extends BaseController {
    @Autowired
    private IActSmsScrapOrderService actSmsScrapOrderService;

    /**
     * 根据业务key获取数据
     *
     * @param businessKey
     * @return smsSupplementaryOrder
     * @author cs
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取数据", response = SmsScrapOrder.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        return actSmsScrapOrderService.getBizInfoByTableId(businessKey);
    }

    /**
     * 报废审核开启流程  提交(编辑、新增提交)
     *
     * @param smsScrapOrder
     * @return R 成功/失败
     */
    @PostMapping("open")
    @OperLog(title = "报废审核开启流程(编辑、新增提交)", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "报废审核开启流程(编辑、新增提交)", response = R.class)
    public R addSave(@RequestBody SmsScrapOrder smsScrapOrder) {
        return actSmsScrapOrderService.startAct(smsScrapOrder,getCurrentUserId());
    }

    /**
     * 报废审核开启流程  提交(列表提交)
     *
     * @param smsScrapOrder
     * @return R 成功/失败
     */
    @PostMapping("openOnlyForList")
    @OperLog(title = "报废审核开启流程(列表提交)", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "报废审核开启流程(列表提交)", response = R.class)
    public R addSaveOnlyForList(@RequestBody SmsScrapOrder smsScrapOrder) {
        return actSmsScrapOrderService.startActOnlyForList(smsScrapOrder,getCurrentUserId());
    }

    /**
     * 报废流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @PostMapping("audit")
    @ApiOperation(value = "报废流程审批 ", response = R.class)
    public R audit(@RequestBody BizAudit bizAudit) {
        //审核
        return actSmsScrapOrderService.audit(bizAudit,getCurrentUserId());
    }
}
