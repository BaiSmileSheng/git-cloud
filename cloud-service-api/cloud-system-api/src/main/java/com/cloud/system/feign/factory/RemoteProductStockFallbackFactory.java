package com.cloud.system.feign.factory;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteProductStockService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteProductStockFallbackFactory implements FallbackFactory<RemoteProductStockService> {

    @Override
    public RemoteProductStockService create(Throwable throwable) {

        return new RemoteProductStockService() {
            /**
             * 定时任务同步成品库存
             *
             * @return
             */
            @Override
            public R timeSycProductStock() {
                log.error(StrUtil.format("RemoteProductStockService.timeSycProductStock错误:{}",throwable.getMessage()));
                return R.error("定时任务同步成品库存熔断");
            }
        };
    }
}
