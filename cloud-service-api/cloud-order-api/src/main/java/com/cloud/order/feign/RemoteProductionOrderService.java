package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.factory.RemoteProductionOrderFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 生产管理 Feign服务层
 *
 * @author cs
 * @date 2020-05-20
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteProductionOrderFallbackFactory.class)
public interface RemoteProductionOrderService {
    /**
     * 根据生产订单号查询排产订单信息
     * @param prodctOrderCode
     * @return OmsProductionOrder
     */
    @GetMapping("productionOrder/selectByProdctOrderCode")
    R selectByProdctOrderCode(@RequestParam("prodctOrderCode") String prodctOrderCode);

    /**
     * 查询排产订单 列表
     * @param productEndDateEnd  基本结束时间 结束值
     * @param actualEndDateStart 实际结束时间 起始值
     * @param actualEndDateEnd 实际结束时间 结束值
     * @return 排产订单 列表
     */
    @GetMapping("productionOrder/listForDelays")
    R listForDelays(@RequestParam("productEndDateEnd") String productEndDateEnd,
                                           @RequestParam("actualEndDateStart") String actualEndDateStart,
                                           @RequestParam("actualEndDateEnd") String actualEndDateEnd);
    /**
     * 根据ID查询
     * @param id
     * @return 排产订单
     */
    @GetMapping("productionOrder/get")
    OmsProductionOrder get(@RequestParam("id") Long id);

    /**
     * 修改保存排产订单
     */
    @PostMapping("productionOrder/update")
    R editSave(@RequestBody OmsProductionOrder omsProductionOrder);

    /**
     * 定时任务SAP获取订单号
     */
    @PostMapping("productionOrder/timeSAPGetProductOrderCode")
    R timeSAPGetProductOrderCode();

    /**
     * 定时任务生成加工结算信息
     */
    @PostMapping("productionOrder/timeInsertSettleList")
    R timeInsertSettleList();

    /**
     * 定时获取入库量
     * @return
     */
    @PostMapping("productionOrder/timeGetConfirmAmont")
    R timeGetConfirmAmont();
}
