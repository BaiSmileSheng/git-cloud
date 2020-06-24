package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.Remote2weeksDemandOrderEditFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 2周需求 Feign服务层
 *
 * @author cs
 * @date 2020-06-23
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = Remote2weeksDemandOrderEditFallbackFactory.class)
public interface Remote2weeksDemandOrderEditService {
    /**
     * SAP601创建订单接口定时任务（ZPP_INT_DDPS_02）
     * @return
     */
    @PostMapping("oms2weeksDemandOrderEdit/queryPlanOrderCodeFromSap601")
    R queryPlanOrderCodeFromSap601();
}
