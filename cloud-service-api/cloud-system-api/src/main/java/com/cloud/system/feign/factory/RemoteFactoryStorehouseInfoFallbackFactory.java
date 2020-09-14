package com.cloud.system.feign.factory;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.feign.RemoteFactoryStorehouseInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class RemoteFactoryStorehouseInfoFallbackFactory implements FallbackFactory<RemoteFactoryStorehouseInfoService> {

    @Override
    public RemoteFactoryStorehouseInfoService create(Throwable throwable) {

        return new RemoteFactoryStorehouseInfoService() {

            /**
             * 查询一个工厂库位
             * @param cdFactoryStorehouseInfo
             * @return
             */
            @Override
            public R findOneByExample(CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
                log.error("RemoteFactoryStorehouseInfoService.findOneByExample错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 根据条件查询
             */
            @Override
            public R findByExample(CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
                log.error("RemoteFactoryStorehouseInfoService.findByExample错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 根据工厂，客户编码分组取接收库位
             * @param dicts
             * @return
             */
            @Override
            public R selectStorehouseToMap(List<Dict> dicts) {
                log.error("RemoteFactoryStorehouseInfoService.selectStorehouseToMap错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 查询工厂库位 列表
             * @param cdFactoryStorehouseInfoReq
             * @return
             */
            @Override
            public R listFactoryStorehouseInfo(String cdFactoryStorehouseInfoReq) {
                log.error("RemoteFactoryStorehouseInfoService.listFactoryStorehouseInfo：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
