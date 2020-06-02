package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteFactoryLineInfoFallbackFactory implements FallbackFactory<RemoteFactoryLineInfoService> {

    @Override
    public RemoteFactoryLineInfoService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteFactoryLineInfoService() {
            /**
             * 查询工厂线体关系
             * @param cdFactoryLineInfo
             * @return List<CdFactoryLineInfo>
             */
            @Override
            public R listByExample(CdFactoryLineInfo cdFactoryLineInfo) {
                return null;
            }

            /**
             * 查询工厂线体关系
             * @param supplierCode
             * @return 逗号分隔线体编号
             */
            @Override
            public R selectLineCodeBySupplierCode(String supplierCode) {
                return null;
            }
        };
    }
}
