package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteSapSystemInterfaceService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteSapSystemInterfaceFallbackFactory implements FallbackFactory<RemoteSapSystemInterfaceService> {

    @Override
    public RemoteSapSystemInterfaceService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteSapSystemInterfaceService() {


            @Override
            public R sycRawMaterialStock() {
                return null;
            }
        };
    }
}
