package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.settle.feign.RemoteSmsQualityScrapOrderLogService;
import com.cloud.settle.feign.RemoteSmsQualityScrapOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSmsQualityScrapOrderLogFallbackFactory implements FallbackFactory<RemoteSmsQualityScrapOrderLogService> {


    @Override
    public RemoteSmsQualityScrapOrderLogService create(Throwable throwable) {
        return new RemoteSmsQualityScrapOrderLogService() {

            /**
             * 根据报废ID查询质量部报废申诉记录
             */
            @Override
            public R getByQualityId(Long qualityId) {
                log.error("RemoteSmsQualityScrapOrderLogService.getByQualityId错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
        };
    }
}
