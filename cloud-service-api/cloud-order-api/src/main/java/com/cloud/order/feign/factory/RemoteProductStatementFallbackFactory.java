package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductStatementService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteProductStatementFallbackFactory implements FallbackFactory<RemoteProductStatementService> {


    @Override
    public RemoteProductStatementService create(Throwable throwable) {

        return new RemoteProductStatementService(){

            /**
             * 定时汇总T-1交付考核报
             * @return
             */
            @Override
            public R timeAddSave() {
                log.error("RemoteProductStatementService.timeAddSave：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
