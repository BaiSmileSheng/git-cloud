package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteCdScrapMonthNoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteCdScrapMonthNoFallbackFactory implements FallbackFactory<RemoteCdScrapMonthNoService> {

    @Override
    public RemoteCdScrapMonthNoService create(Throwable throwable) {
        log.error("RemoteCdScrapMonthNoService错误信息：{}",throwable.getMessage());
        return new RemoteCdScrapMonthNoService() {
            /**
             * 根据月份查询汇率
             * @param yearMouth
             * @return rate
             */
            @Override
            public R findOne(String yearMouth,String factoryCode) {
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
