package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.factory.RemoteProductionOrderFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 报废管理 Feign服务层
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
    OmsProductionOrder selectByProdctOrderCode(@RequestParam("prodctOrderCode") String prodctOrderCode);
}
