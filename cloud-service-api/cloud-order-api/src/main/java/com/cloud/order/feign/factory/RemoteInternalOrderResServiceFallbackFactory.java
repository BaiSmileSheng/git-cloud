package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteInternalOrderResService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteInternalOrderResServiceFallbackFactory implements FallbackFactory<RemoteInternalOrderResService> {


    @Override
    public RemoteInternalOrderResService create(Throwable throwable) {
        return new RemoteInternalOrderResService(){
            @Override
            public R queryAndInsertDemandPRFromSap800Friday() {
                return null;
            }

            @Override
            public R queryAndInsertDemandPRFromSap800Monday() {
                return null;
            }
        };
    }
}
