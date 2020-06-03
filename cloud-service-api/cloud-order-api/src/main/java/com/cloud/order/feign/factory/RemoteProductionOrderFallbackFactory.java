package com.cloud.order.feign.factory;

import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteProductionOrderFallbackFactory implements FallbackFactory<RemoteProductionOrderService> {


    @Override
    public RemoteProductionOrderService create(Throwable throwable) {
        return new RemoteProductionOrderService(){

            @Override
            public OmsProductionOrder selectByProdctOrderCode(String prodctOrderCode) {
                return null;
            }
        };
    }
}
