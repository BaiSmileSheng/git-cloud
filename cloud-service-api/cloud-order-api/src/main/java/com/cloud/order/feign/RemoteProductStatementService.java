package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteProductStatementFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * T-1交付考核报表  提供者
 *
 * @author lihongxia
 * @date 2020-08-07
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteProductStatementFallbackFactory.class)
public interface RemoteProductStatementService {

    /**
     * 定时汇总T-1交付考核报
     * @return
     */
    @PostMapping("productStatement/timeAddSave")
    R timeAddSave();
}
