package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteCdMouthRateFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户 Feign服务层
 *
 * @author cs
 * @date 2020-06-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdMouthRateFallbackFactory.class)
public interface RemoteCdMouthRateService {
    /**
     * 根据月份查询汇率
     *
     * @param yearMouth
     * @return rate
     */
    @GetMapping(value = "mouthRate/findRate")
    R findRateByYearMouth(@RequestParam(value = "yearMouth") String yearMouth,@RequestParam(value = "currency") String currency);
}
