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

        return new RemoteSapSystemInterfaceService() {

            /**
             * 定时同步原材料库存
             *
             * @return
             */
            @Override
            public R sycRawMaterialStock() {
                log.error("定时同步原材料库存熔断 error:{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

            /**
             * 定时获取BOM清单数据
             * @return
             */
            @Override
            public R sycBomInfo() {
                log.error("定时获取BOM清单数据熔断 error:{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }
        };
    }
}
