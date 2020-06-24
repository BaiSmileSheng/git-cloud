package com.cloud.activiti.controller;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.service.IActOmsProductionOrderService;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.system.domain.entity.SysUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排产订单审核流程
 * Description:
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/24
 */

@RestController
@RequestMapping("actOmsProductionOrder")
@Api(tags = "排产订单审核流程 ")
public class ActOmsProductionOrderController extends BaseController {
    @Autowired
    private IActOmsProductionOrderService actOmsProductionOrderService;

    /**
     * 根据业务key获取排产订单信息
     *
     * @param businessKey biz_business的主键
     * @return 查询结果包含 排产订单信息
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取排产订单信息", response = OmsProductionOrder.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        return actOmsProductionOrderService.getBizInfoByTableId(businessKey);
    }
    /**
     * Description:  开启审批流
     * Param: [key, orderId, orderCode, userId, title]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    @PostMapping("startActProcess")
    @ApiOperation(value = "排产订单开启审批流程", response = OmsProductionOrder.class)
    @HasPermissions("order:productOrder:commit")
    public R startActProcess(String key, String orderId, String orderCode, Long userId,String title) {
        return actOmsProductionOrderService.startActProcess(key, orderId, orderCode, userId,title);
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
    @HasPermissions("order:productOrder:audit")
    public R audit(@RequestBody BizAudit bizAudit) {
        return actOmsProductionOrderService.audit(bizAudit, getCurrentUserId());
    }


}
