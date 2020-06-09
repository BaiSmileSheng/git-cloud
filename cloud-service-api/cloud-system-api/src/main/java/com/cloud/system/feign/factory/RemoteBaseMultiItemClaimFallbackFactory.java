package com.cloud.system.feign.factory;

import com.cloud.system.domain.webServicePO.BaseClaimResponse;
import com.cloud.system.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import com.cloud.system.feign.RemoteBaseMultiItemClaimService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 报账单创建接口 Feign服务层
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@Slf4j
@Component
public class RemoteBaseMultiItemClaimFallbackFactory implements FallbackFactory<RemoteBaseMultiItemClaimService> {

    @Override
    public RemoteBaseMultiItemClaimService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteBaseMultiItemClaimService() {

            /**
             * 单据创建接口（支持多明细）
             */
            @Override
            public BaseClaimResponse createMultiItemClaim(BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest) {
                return null;
            }
        };
    }
}
