package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.feign.RemoteMaterialService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteMaterialFactory implements FallbackFactory<RemoteMaterialService> {
    @Override
    public RemoteMaterialService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteMaterialService() {
            @Override
            public R saveMaterialInfo() {
                return null;
            }

            @Override
            public R updateUphBySap() {
                return null;
            }

            /**
             * 根据物料号查询物料信息
             * @param materialCode
             * @return
             */
            @Override
            public CdMaterialInfo getByMaterialCode(String materialCode) {
                return null;
            }
        };
    }
}
