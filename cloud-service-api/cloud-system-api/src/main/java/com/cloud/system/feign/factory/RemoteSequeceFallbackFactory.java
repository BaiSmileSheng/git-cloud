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

        return new RemoteSequeceService() {
            @Override
            public String selectSeq(String name, int length) {
                log.error("RemoteSequeceService.selectSeq错误信息：{}",throwable.getMessage());
                return null;
            }
        };
    }
}
