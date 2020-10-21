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

import java.util.List;

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
     * @return 排产订单 列表
     */
    @GetMapping("productionOrder/listForDelays")
    R listForDelays();
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

    /**
     * 根据主键修改排产订单
     * @param omsProductionOrderList
     * @return
     */
    @PostMapping("productionOrder/updateBatchByPrimary")
    R updateBatchByPrimary(@RequestBody List<OmsProductionOrder> omsProductionOrderList);
    /**
     * 获取初始化中状态的排产订单
     * @param
     * @return
     */
    @PostMapping("productionOrder/selectByStatusAct")
    R selectByStatusAct();
    /**
     * 定时任务校验排产订单审批流
     * @param
     * @return
     */
    @PostMapping("productionOrder/checkProductOrderAct")
    R checkProductOrderAct(@RequestBody List<OmsProductionOrder> list);
}
