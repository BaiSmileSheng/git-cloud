package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.feign.RemoteFactoryInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component
public class RemoteFactoryInfoFallbackFactory implements FallbackFactory<RemoteFactoryInfoService> {

    @Override
    public RemoteFactoryInfoService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteFactoryInfoService() {
            /**
             * 查询工厂信息
             * @param factoryCode
             * @return null
             */
            @Override
            public CdFactoryInfo selectOneByFactory(String factoryCode) {
                return null;
            }
            /**
             * 根据公司V码查询
             * @param companyCodeV
             * @return null
             */
            @Override
            public Map<String, CdFactoryInfo> selectAllByCompanyCodeV(String companyCodeV) {
                return null;
            }
        };
    }
}
