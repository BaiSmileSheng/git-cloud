package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductionOrderDetailService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteProductionOrderDetailFallbackFactory implements FallbackFactory<RemoteProductionOrderDetailService> {


    @Override
    public RemoteProductionOrderDetailService create(Throwable throwable) {

        return new RemoteProductionOrderDetailService(){

            @Override
            public R selectDetailByOrderAct(List<String> orderCodes) {
                log.error("RemoteProductionOrderDetailService.selectDetailByOrderAct(生产订单明细)错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }
        };
    }
}
