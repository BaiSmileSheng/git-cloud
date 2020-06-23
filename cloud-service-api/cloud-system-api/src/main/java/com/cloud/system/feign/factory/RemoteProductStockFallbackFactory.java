package com.cloud.system.feign.factory;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.feign.RemoteProductStockService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


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

            /**
             * 根据Example查询一条数据
             * @param cdProductStock
             * @return
             */
            @Override
            public R findOneByExample(CdProductStock cdProductStock) {
                log.error(StrUtil.format("RemoteProductStockService.findOneByExample错误:{}",throwable.getMessage()));
                return R.error("服务器拥挤，请稍后再试");
            }

            /**
             * 根据工厂，专用号分组取成品库存
             * @param dicts
             * @return
             */
            @Override
            public R selectProductStockToMap(List<Dict> dicts) {
                log.error(StrUtil.format("RemoteProductStockService.selectProductStockToMap错误:{}",throwable.getMessage()));
                return R.error("服务器拥挤，请稍后再试");
            }
        };
    }
}
