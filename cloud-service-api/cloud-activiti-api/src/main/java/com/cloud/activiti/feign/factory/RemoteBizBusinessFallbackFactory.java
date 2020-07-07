package com.cloud.activiti.feign.factory;

import com.cloud.activiti.feign.RemoteBizBusinessService;
import com.cloud.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteBizBusinessFallbackFactory implements FallbackFactory<RemoteBizBusinessService> {

    @Override
    public RemoteBizBusinessService create(Throwable throwable) {
        return new RemoteBizBusinessService() {
            @Override
            public R selectByKeyAndTable(String procDefKey, String tableId) {
                log.error("RemoteBizBusinessService，selectByKeyAndTable{}：" + throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }

        };
    }
}
