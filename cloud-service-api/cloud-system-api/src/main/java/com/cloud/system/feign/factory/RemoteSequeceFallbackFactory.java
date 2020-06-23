package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteSequeceService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSequeceFallbackFactory implements FallbackFactory<RemoteSequeceService> {
    @Override
    public RemoteSequeceService create(Throwable throwable) {
        return new RemoteSequeceService() {
            @Override
            public R selectSeq(String name, int length) {
                log.error("查序列号异常 name:{} e：{}",name,throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }
        };
    }
}
