package com.cloud.system.feign.factory;

import com.cloud.system.feign.RemoteSequeceService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSequeceFallbackFactory implements FallbackFactory<RemoteSequeceService> {
    @Override
    public RemoteSequeceService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteSequeceService() {
            @Override
            public String selectSeq(String name, int length) {
                return null;
            }
        };
    }
}
