package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.feign.RemoteCdProductWarehouseService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:  成品在库库存明细
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/16
 */
@Component
@Slf4j
public class RemoteCdProductWarehouseFallbackFactory implements FallbackFactory<RemoteCdProductWarehouseService> {
    @Override
    public RemoteCdProductWarehouseService create(Throwable throwable) {
        return new RemoteCdProductWarehouseService() {
            /**
             * Description:  查询单条库位库存
             * Param: [cdProductWarehouse]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/16
             */
            @Override
            public R queryOneByExample(CdProductWarehouse cdProductWarehouse) {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
            /**
             * Description:  查询库位库存List
             * Param: [list]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/17
             */
            @Override
            public R queryByList(List<CdProductWarehouse> list) {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
