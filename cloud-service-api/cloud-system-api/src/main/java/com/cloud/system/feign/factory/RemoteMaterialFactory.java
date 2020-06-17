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
        log.error("RemoteMaterialService错误信息：{}",throwable.getMessage());
        return new RemoteMaterialService() {
            @Override
            public R saveMaterialInfo() {
                return R.error("服务器拥挤，请稍后再试！");
            }

            @Override
            public R updateUphBySap() {
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 根据物料号查询物料信息
             * @param materialCode
             * @return
             */
            @Override
            public R getByMaterialCode(String materialCode) {
                return R.error("服务拥挤请稍后再试");
            }
        };
    }
}
