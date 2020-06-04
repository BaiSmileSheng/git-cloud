package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.CdSettleRatio;
import com.cloud.system.feign.RemoteSettleRatioService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSettleRatioFallbackFactory implements FallbackFactory<RemoteSettleRatioService> {
    @Override
    public RemoteSettleRatioService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteSettleRatioService() {

            /**
             * 查询结算索赔系数
             * @param claimType 索赔类型
             * @return 结算索赔系数
             */
            @Override
            public CdSettleRatio selectByClaimType(String claimType) {
                return null;
            }
        };
    }
}
