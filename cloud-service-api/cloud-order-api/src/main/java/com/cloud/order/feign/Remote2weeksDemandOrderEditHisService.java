package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.Remote2weeksDemandOrderEditHisFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 2周需求 Feign服务层
 *
 * @author cs
 * @date 2020-06-23
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = Remote2weeksDemandOrderEditHisFallbackFactory.class)
public interface Remote2weeksDemandOrderEditHisService {

    /**
     * 根据id 查询
     * @param id
     * @return
     */
    @GetMapping("oms2weeksDemandOrderEditHis/get")
    R get(@RequestParam("id") Long id);

}
