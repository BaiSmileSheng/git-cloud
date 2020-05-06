package com.cloud.system.feign.factory;

import com.cloud.system.feign.RemoteUserScopeService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteUserSocopeFallbackFactory implements FallbackFactory<RemoteUserScopeService> {

    @Override
    public RemoteUserScopeService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteUserScopeService() {

            @Override
            public String selectDataScopeIdByUserId(Long userId) {
                return null;
            }
        };
    }
}
