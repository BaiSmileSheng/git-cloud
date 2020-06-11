package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteQryPaysSoapService;
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
public class RemoteQryPaysSoapFallbackFactory implements FallbackFactory<RemoteQryPaysSoapService> {

    @Override
    public RemoteQryPaysSoapService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteQryPaysSoapService() {

            /**
             * 定时任务调用查询付款结果更新月度结算信息
             * @return
             */
            @Override
            public R updateKmsStatus() {
                return null;
            }
        };
    }
}
