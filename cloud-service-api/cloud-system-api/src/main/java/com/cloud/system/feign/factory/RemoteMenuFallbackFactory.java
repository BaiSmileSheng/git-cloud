package com.cloud.system.feign.factory;

import java.util.Set;

import com.cloud.system.feign.RemoteMenuService;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RemoteMenuFallbackFactory implements FallbackFactory<RemoteMenuService> {
    @Override
    public RemoteMenuService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteMenuService() {

            @Override
            public Set<String> selectPermsByUserId(Long userId) {
                return null;
            }
        };
    }
}
