package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteCdMouthRateService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteCdMouthRateFallbackFactory implements FallbackFactory<RemoteCdMouthRateService> {

    @Override
    public RemoteCdMouthRateService create(Throwable throwable) {
        return new RemoteCdMouthRateService() {
            /**
             * 根据月份查询汇率
             * @param yearMouth
             * @return rate
             */
            @Override
            public R findRateByYearMouth(String yearMouth) {
                return null;
            }
        };
    }
}
