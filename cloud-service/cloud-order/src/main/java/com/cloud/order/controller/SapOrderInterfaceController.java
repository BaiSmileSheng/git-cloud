package com.cloud.order.controller;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.service.IOrderFromSap601InterfaceService;
import com.cloud.order.service.IOrderFromSap800InterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
/**
 * @Description: Order模块对SAP系统接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/8
 */
@RestController
@RequestMapping("sapOrder")
@Api(tags = "Order模块对SAP系统接口")
public class SapOrderInterfaceController {
    @Autowired
    private IOrderFromSap601InterfaceService IOrderFromSap601InterfaceService;
    @Autowired
    private IOrderFromSap800InterfaceService IOrderFromSap800InterfaceService;
    /**
     * @Description: 获取SAP601系统生产订单
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/4
     */
    @GetMapping("queryProductOrder")
    @ApiOperation(value = "获取SAP601系统生产订单 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "list",value = "排产订单号",required = true)})
    public R queryProductOrder(List<OmsProductionOrder> list) {
        return IOrderFromSap601InterfaceService.queryProductOrderFromSap601(list);
    }



    /**
     * @Description: SAP601创建生产订单
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/4
     */
    @GetMapping("createProductOrder")
    @ApiOperation(value = "SAP601创建生产订单 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "list",value = "排产订单信息",required = true)})
    public R createProductOrder(List<OmsProductionOrder> list) {
        return IOrderFromSap601InterfaceService.createProductOrderFromSap601(list);
    }



    /**
     * @Description: 获取SAP800系统13周PR需求
     * @Param: [startDate, endDate]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @GetMapping("queryDemandPR")
    @ApiOperation(value = "获取SAP800系统13周PR需求 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate",value = "开始日期",required = true),
            @ApiImplicitParam(name = "endDate",value = "结束日期",required = true)})
    public R queryDemandPR(Date startDate, Date endDate) {
        return IOrderFromSap800InterfaceService.queryDemandPRFromSap800(startDate,endDate);
    }



    /**
     * @Description: 获取SAP800系统PO真单
     * @Param: [startDate, endDate]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @GetMapping("queryDemandPO")
    @ApiOperation(value = "获取SAP800系统PO真单 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate",value = "开始日期",required = true),
            @ApiImplicitParam(name = "endDate",value = "结束日期",required = true)})
    public R queryDemandPO(Date startDate, Date endDate) {
        return IOrderFromSap800InterfaceService.queryDemandPOFromSap800(startDate,endDate);
    }


}
