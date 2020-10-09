package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteProductDifferenceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteProductDifferenceFallbackFactory.class)
public interface RemoteProductDifferenceService {
    /**
     * Description:  生成外单排产差异报表
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/9/30
     */
    @PostMapping("difference/timeProductDiffTask")
    R timeProductDiffTask();
}
