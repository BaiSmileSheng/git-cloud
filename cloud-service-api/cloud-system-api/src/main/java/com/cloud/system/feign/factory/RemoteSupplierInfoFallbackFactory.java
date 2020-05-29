package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.CdSupplierInfo;
import com.cloud.system.feign.RemoteSupplierInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSupplierInfoFallbackFactory implements FallbackFactory<RemoteSupplierInfoService> {

    @Override
    public RemoteSupplierInfoService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteSupplierInfoService() {

            /**
             * 根据登录名查询供应商信息
             * @param loginName
             * @return CdSupplierInfo
             */
            @Override
            public CdSupplierInfo getByNick(String loginName) {
                return null;
            }
        };
    }
}
