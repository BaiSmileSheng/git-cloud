package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.Remote2weeksDemandOrderEditHisService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Remote2weeksDemandOrderEditHisFallbackFactory implements FallbackFactory<Remote2weeksDemandOrderEditHisService> {


    @Override
    public Remote2weeksDemandOrderEditHisService create(Throwable throwable) {

        return new Remote2weeksDemandOrderEditHisService(){
            @Override
            public R get(Long id) {
                log.error("Remote2weeksDemandOrderEditHisService.get熔断错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
