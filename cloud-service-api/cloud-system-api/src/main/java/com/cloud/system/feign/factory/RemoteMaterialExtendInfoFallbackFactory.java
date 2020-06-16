package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteMaterialExtendInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteMaterialExtendInfoFallbackFactory implements FallbackFactory<RemoteMaterialExtendInfoService> {

    @Override
    public RemoteMaterialExtendInfoService create(Throwable throwable) {
        return new RemoteMaterialExtendInfoService() {

            /**
             * 定时任务传输成品物料接口
             *
             * @return
             */
            @Override
            public R timeSycMaterialCode() {
                log.error("定时任务传输成品物料接口熔断 error:{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }
        };
    }
}
