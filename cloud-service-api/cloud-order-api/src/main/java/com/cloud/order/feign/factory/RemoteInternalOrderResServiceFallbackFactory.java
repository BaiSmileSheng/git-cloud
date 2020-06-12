package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteInternalOrderResService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class RemoteInternalOrderResServiceFallbackFactory implements FallbackFactory<RemoteInternalOrderResService> {


    @Override
    public RemoteInternalOrderResService create(Throwable throwable) {
        return new RemoteInternalOrderResService(){
            /**
             * 根据时间从800获取PR
             * @param startDate
             * @param endDate
             * @return
             */
            @Override
            public R queryAndInsertDemandPRFromSap800(Date startDate, Date endDate) {
                return null;
            }
        };
    }
}
