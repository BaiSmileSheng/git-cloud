package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteMouthSettleService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteMouthSettleFallbackFactory implements FallbackFactory<RemoteMouthSettleService> {


    @Override
    public RemoteMouthSettleService create(Throwable throwable) {
        return new RemoteMouthSettleService() {
            /**
             * 月度结算定时任务
             * @return null
             */
            @Override
            public R countMonthSettle() {
                log.error("RemoteMouthSettleService.countMonthSettle月度结算定时任务错误：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
