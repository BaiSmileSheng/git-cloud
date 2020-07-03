package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.feign.RemoteCdRawMaterialStockService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RemoteCdRawMaterialStockFallbackFactory implements FallbackFactory<RemoteCdRawMaterialStockService> {
    @Override
    public RemoteCdRawMaterialStockService create(Throwable throwable) {
        return new RemoteCdRawMaterialStockService() {
            /**
             * Description:  根据对象查询原材料库存
             * Param: [cdRawMaterialStock]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/28
             */
            @Override
            public R selectByList(List<CdRawMaterialStock> list) {
                log.error("根据对象查询原材料库存,原因{}："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
