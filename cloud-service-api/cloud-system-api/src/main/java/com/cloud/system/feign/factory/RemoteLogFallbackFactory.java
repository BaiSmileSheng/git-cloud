package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.SysOperLog;
import org.springframework.stereotype.Component;

import com.cloud.system.domain.entity.SysLogininfor;
import com.cloud.system.feign.RemoteLogService;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService> {
    @Override
    public RemoteLogService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteLogService() {
            @Override
            public void insertOperlog(SysOperLog operLog) {
            }

            @Override
            public void insertLoginlog(SysLogininfor logininfor) {
            }
        };
    }
}
