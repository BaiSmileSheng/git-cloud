package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteFactoryInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteFactoryInfoFallbackFactory implements FallbackFactory<RemoteFactoryInfoService> {

    @Override
    public RemoteFactoryInfoService create(Throwable throwable) {

        return new RemoteFactoryInfoService() {
            /**
             * 查询工厂信息
             * @param factoryCode
             * @return null
             */
            @Override
            public R selectOneByFactory(String factoryCode) {
                log.error("RemoteFactoryInfoService.selectOneByFactory错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
            /**
             * 根据公司V码查询
             * @param companyCodeV
             * @return null
             */
            @Override
            public R selectAllByCompanyCodeV(String companyCodeV) {
                log.error("RemoteFactoryInfoService.selectAllByCompanyCodeV错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }

            /**
             * 获取所有公司编码
             * @return
             */
            @Override
            public R getAllCompanyCode() {
                log.error("RemoteFactoryInfoService.getAllCompanyCode错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }

            @Override
            public R listAll() {
                log.error("RemoteFactoryInfoService.listCdFactoryInfo错误信息 e：{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
