package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.system.feign.RemoteCdProductOverdueService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Component
public class RemoteCdProductOverdueFallbackFactory  implements FallbackFactory<RemoteCdProductOverdueService> {
    @Override
    public RemoteCdProductOverdueService create(Throwable throwable) {
        return new RemoteCdProductOverdueService() {

            /**
             * Description: 根据工厂、物料号查询超期库存
             * Param: [cdProductOverdue]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/24
             */
            @Override
            public R selectOverStockByFactoryAndMaterial(@RequestBody List<String> productMaterialCodeList) {
                log.error("根据工厂、物料号查询超期库存失败，原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
