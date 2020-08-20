package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.factory.RemoteMouthSettleFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 月度结算 Feign服务层
 *
 * @author cs
 * @date 2020-06-05
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteMouthSettleFallbackFactory.class)
public interface RemoteMouthSettleService {
    /**
     * 月度结算定时任务
     * @return R
     */
    @PostMapping("mouthSettle/countMonthSettle")
    R countMonthSettle();

    /**
     * 定时任务更新索赔单已兑现的为已结算
     * @return R
     */
    @PostMapping("mouthSettle/timeUpdateSettle")
    R timeUpdateSettle();


}
