package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.feign.RemoteOmsRealOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteOmsRealOrderFallbackFactory implements FallbackFactory<RemoteOmsRealOrderService> {


    @Override
    public RemoteOmsRealOrderService create(Throwable throwable) {

        return new RemoteOmsRealOrderService(){

            /**
             * 定时任务每天在获取到PO信息后 进行需求汇总
             */
            @Override
            public R timeCollectToOmsRealOrder() {
                log.error("RemoteOmsRealOrderService.timeCollectToOmsRealOrder熔断错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            @Override
            public R get(Long id) {
                log.error("RemoteOmsRealOrderService.get熔断错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            @Override
            public R editSave(OmsRealOrder omsRealOrder) {
                log.error("RemoteOmsRealOrderService.editSave熔断错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
