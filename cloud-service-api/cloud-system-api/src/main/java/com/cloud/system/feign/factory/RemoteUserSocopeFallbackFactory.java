package com.cloud.system.feign.factory;

import com.cloud.system.feign.RemoteUserScopeService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteUserSocopeFallbackFactory implements FallbackFactory<RemoteUserScopeService> {

    @Override
    public RemoteUserScopeService create(Throwable throwable) {

        return new RemoteUserScopeService() {


            /**
             * 根据用户Id和类型获取用户物料权限
             * @param userId
             * @param type
             * @return
             */
            @Override
            public String selectDataScopeIdByUserIdAndType(Long userId, String type) {
                log.error("RemoteUserScopeService.selectDataScopeIdByUserIdAndType错误信息：{}",throwable.getMessage());
                return null;
            }
        };
    }
}
