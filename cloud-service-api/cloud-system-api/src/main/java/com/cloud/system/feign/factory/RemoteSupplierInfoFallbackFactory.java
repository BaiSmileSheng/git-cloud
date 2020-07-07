package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
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

        return new RemoteSupplierInfoService() {

            /**
             * 根据登录名查询供应商信息
             * @param loginName
             * @return CdSupplierInfo
             */
            @Override
            public CdSupplierInfo getByNick(String loginName) {
                log.error("RemoteSupplierInfoService.getByNick错误信息：{}",throwable.getMessage());
                return null;
            }

            @Override
            public R selectOneBySupplierCode(String supplierCode) {
                log.error("RemoteSupplierInfoService.selectOneBySupplierCode：{}",throwable.getMessage());
                return R.error("服务拥挤,请稍后再试!");
            }
        };
    }
}
