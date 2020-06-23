package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteInternalOrderResService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteInternalOrderResServiceFallbackFactory implements FallbackFactory<RemoteInternalOrderResService> {


    @Override
    public RemoteInternalOrderResService create(Throwable throwable) {

        return new RemoteInternalOrderResService(){
            /**
             * SAP800获取PR定时任务(周五)
             * @return
             */
            @Override
            public R queryAndInsertDemandPRFromSap800Friday() {
                log.error("RemoteInternalOrderResService.queryAndInsertDemandPRFromSap800Friday(PR/PO熔断)错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * SAP800获取PR定时任务(周一)
             *
             * @return
             */
            @Override
            public R queryAndInsertDemandPRFromSap800Monday() {
                log.error("RemoteInternalOrderResService.queryAndInsertDemandPRFromSap800Monday(PR/PO熔断)错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            @Override
            public R timeInsertFromSAP() {
                log.error("RemoteInternalOrderResService.timeInsertFromSAP 错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
