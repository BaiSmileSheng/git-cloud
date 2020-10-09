package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductDifferenceService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RemoteProductDifferenceFallbackFactory implements FallbackFactory<RemoteProductDifferenceService> {
    @Override
    public RemoteProductDifferenceService create(Throwable throwable) {
        return new RemoteProductDifferenceService() {
            /**
             * Description:  生成外单排产差异报表
             * Param: []
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/9/30
             */
            @Override
            public R timeProductDiffTask() {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
