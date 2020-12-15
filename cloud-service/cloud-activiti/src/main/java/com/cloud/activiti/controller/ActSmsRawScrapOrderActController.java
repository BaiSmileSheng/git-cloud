package com.cloud.activiti.controller;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.service.IActSmsRawScrapOrderService;
import com.cloud.activiti.service.IActSmsScrapOrderService;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.SysUserVo;
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
@RequestMapping("actRawScrapOrder")
@Api(tags = "报废申请单审核流程 ")
public class ActSmsRawScrapOrderActController extends BaseController {
    @Autowired
    private IActSmsRawScrapOrderService actSmsRawScrapOrderService;

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
        return actSmsRawScrapOrderService.getBizInfoByTableId(businessKey);
    }

    /**
     * 原材料报废审核开启流程  提交(编辑、新增提交)
     *
     * @param actBusinessVo
     * @return R 成功/失败
     */
    @PostMapping("open")
    @OperLog(title = "原材料报废审核开启流程(编辑、新增提交)", businessType = BusinessType.INSERT)
    @ApiOperation(value = "原材料报废审核开启流程(编辑、新增提交)", response = R.class)
    @HasPermissions("settle:actRawScrapOrder:commit")
    public R addSave(@RequestBody ActBusinessVo actBusinessVo) {
        return actSmsRawScrapOrderService.startAct(actBusinessVo);
    }

    /**
     * Description:  推进流程
     * Param: [bizAudit]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    @PostMapping("audit")
    @ApiOperation(value = "推进流程 ", response = R.class)
    @HasPermissions("settle:actRawScrapOrder:audit")
    public R audit(@RequestBody BizAudit bizAudit) {
        return actSmsRawScrapOrderService.audit(bizAudit, getCurrentUserId());
    }
}
