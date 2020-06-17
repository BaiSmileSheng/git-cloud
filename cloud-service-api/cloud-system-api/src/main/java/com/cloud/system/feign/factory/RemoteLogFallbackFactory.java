package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
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
        log.error("RemoteLogService错误信息：{}",throwable.getMessage());
        return new RemoteLogService() {
            @Override
            public R insertOperlog(SysOperLog operLog) {
                return R.error("前方拥挤，请稍等~");
            }

            @Override
            public void insertLoginlog(SysLogininfor logininfor) {
            }
        };
    }
}
