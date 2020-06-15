package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteDemandOrderGatherService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteDemandOrderGatherFallbackFactory implements FallbackFactory<RemoteDemandOrderGatherService> {


    @Override
    public RemoteDemandOrderGatherService create(Throwable throwable) {
        log.error("需求汇总熔断错误信息：{}",throwable.getMessage());
        return new RemoteDemandOrderGatherService(){
            /**
             * 周五需求数据汇总
             * @return
             */
            @Override
            public R gatherDemandOrderFriday() {
                return R.error();
            }
            /**
             * 周一需求数据汇总
             *
             * @return
             */
            @Override
            public R gatherDemandOrderMonday() {
                return R.error();
            }
        };
    }
}
