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
        log.error("PR/PO熔断错误信息：{}",throwable.getMessage());
        return new RemoteInternalOrderResService(){
            /**
             * SAP800获取PR定时任务(周五)
             * @return
             */
            @Override
            public R queryAndInsertDemandPRFromSap800Friday() {
                return R.error();
            }

            /**
             * SAP800获取PR定时任务(周一)
             *
             * @return
             */
            @Override
            public R queryAndInsertDemandPRFromSap800Monday() {
                return R.error();
            }
        };
    }
}
