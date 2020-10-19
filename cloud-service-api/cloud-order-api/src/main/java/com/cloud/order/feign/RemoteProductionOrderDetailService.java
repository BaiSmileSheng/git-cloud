package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.factory.RemoteProductionOrderDetailFallbackFactory;
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
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteProductionOrderDetailFallbackFactory.class)
public interface RemoteProductionOrderDetailService {
    /**
     * Description:  排产订单审批流程校验查询明细
     * Param: [orderCodes]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/10/19
     */
    @PostMapping("productOrderDetail/selectDetailByOrderAct")
    public R selectDetailByOrderAct(@RequestParam("orderCodes") String orderCodes);
}
