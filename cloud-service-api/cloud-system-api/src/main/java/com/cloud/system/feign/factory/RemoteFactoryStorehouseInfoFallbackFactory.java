package com.cloud.system.feign.factory;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.feign.RemoteFactoryStorehouseInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class RemoteFactoryStorehouseInfoFallbackFactory implements FallbackFactory<RemoteFactoryStorehouseInfoService> {

    @Override
    public RemoteFactoryStorehouseInfoService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteFactoryStorehouseInfoService() {

            /**
             * 查询一个工厂库位
             * @param cdFactoryStorehouseInfo
             * @return
             */
            @Override
            public R findOneByExample(CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
                return null;
            }
            /**
             * 根据工厂，客户编码分组取接收库位
             * @param dicts
             * @return
             */
            @Override
            public Map<String, Map<String, String>> selectStorehouseToMap(List<Dict> dicts) {
                return null;
            }
        };
    }
}
