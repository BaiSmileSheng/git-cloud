package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.Remote2weeksDemandOrderEditService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Remote2weeksDemandOrderEditFallbackFactory implements FallbackFactory<Remote2weeksDemandOrderEditService> {


    @Override
    public Remote2weeksDemandOrderEditService create(Throwable throwable) {

        return new Remote2weeksDemandOrderEditService(){

            /**
             * SAP601创建订单接口定时任务（ZPP_INT_DDPS_02）
             * @return
             */
            @Override
            public R queryPlanOrderCodeFromSap601() {
                log.error("Remote2weeksDemandOrderEditService.queryPlanOrderCodeFromSap601熔断错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
