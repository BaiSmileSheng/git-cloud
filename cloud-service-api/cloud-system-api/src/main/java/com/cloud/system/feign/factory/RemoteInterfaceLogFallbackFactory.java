package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.feign.RemoteInterfaceLogService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author Lihongxia
 * @Date 2020-05-20
 */
@Slf4j
@Component
public class RemoteInterfaceLogFallbackFactory implements FallbackFactory<RemoteInterfaceLogService> {
    @Override
    public RemoteInterfaceLogService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteInterfaceLogService(){

            @Override
            public R saveInterfaceLog(SysInterfaceLog sysInterfaceLog) {
                return null;
            }

            @Override
            public R updateInterfaceLog(SysInterfaceLog sysInterfaceLog) {
                return null;
            }
        };
    }
}
