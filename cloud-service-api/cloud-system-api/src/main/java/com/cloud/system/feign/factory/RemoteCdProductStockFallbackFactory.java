package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.feign.RemoteCdProductStockService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:  成品库存信息
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/16
 */
@Component
@Slf4j
public class RemoteCdProductStockFallbackFactory implements FallbackFactory<RemoteCdProductStockService> {
    @Override
    public RemoteCdProductStockService create(Throwable throwable) {
        return new RemoteCdProductStockService(){
            /**
             * Description:  根据生产工厂、成品专用号查询成品库存
             * Param: [cdProductStock]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/16
             */
            @Override
            public R queryOneByFactoryAndMaterial(List<CdProductStock> list) {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
            /**
             * Description:  根据生产工厂、成品专用号查询成品库存
             * Param: [cdProductStock]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/30
             */
            @Override
            public R findOneByExample(CdProductStock cdProductStock) {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
