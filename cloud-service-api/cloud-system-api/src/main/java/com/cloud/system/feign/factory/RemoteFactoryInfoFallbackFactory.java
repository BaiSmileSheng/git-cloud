package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.feign.RemoteFactoryInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteFactoryInfoFallbackFactory implements FallbackFactory<RemoteFactoryInfoService> {

    @Override
    public RemoteFactoryInfoService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteFactoryInfoService() {

            @Override
            public CdFactoryInfo selectOneByFactory(String factoryCode) {
                return null;
            }
        };
    }
}
