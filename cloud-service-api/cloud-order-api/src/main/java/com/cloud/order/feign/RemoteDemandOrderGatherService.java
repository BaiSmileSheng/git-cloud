package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteDemandOrderGatherFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 需求数据接入（800PR） Feign服务层
 *
 * @author cs
 * @date 2020-06-12
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteDemandOrderGatherFallbackFactory.class)
public interface RemoteDemandOrderGatherService {
    /**
     * 周五需求数据汇总
     * @return
     */
    @GetMapping("demandOrderGather/gatherDemandOrderFriday")
    R gatherDemandOrderFriday();

    /**
     * 周一需求数据汇总
     *
     * @return
     */
    @GetMapping("demandOrderGather/gatherDemandOrderMonday")
    R gatherDemandOrderMonday();
}
