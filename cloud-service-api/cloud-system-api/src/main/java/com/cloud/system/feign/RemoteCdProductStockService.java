package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.feign.factory.RemoteCdProductStockFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Description:  成品库存信息
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/16
 */

@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdProductStockFallbackFactory.class)
public interface RemoteCdProductStockService {
    /**
     * Description:  根据生产工厂、成品专用号查询成品库存
     * Param: [cdProductStock]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    @PostMapping("productStock/queryOneByFactoryAndMaterial")
    R queryOneByFactoryAndMaterial(@RequestBody List<CdProductStock> list);
    /**
     * Description:  根据生产工厂、成品专用号查询成品库存
     * Param: [cdProductStock]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/30
     */
    @PostMapping("productStock/findOneByExample")
    R findOneByExample(@RequestBody CdProductStock cdProductStock);
}
