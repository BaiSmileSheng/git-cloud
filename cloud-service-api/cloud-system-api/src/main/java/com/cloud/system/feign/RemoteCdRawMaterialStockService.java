package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.feign.factory.RemoteCdRawMaterialStockFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Description:  原材料库存
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/28
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdRawMaterialStockFallbackFactory.class)
public interface RemoteCdRawMaterialStockService {
    /**
     * Description:  根据对象查询原材料库存
     * Param: [cdRawMaterialStock]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/28
     */
    @PostMapping("rawMaterialStock/selectByList")
    R selectByList(@RequestBody List<CdRawMaterialStock> list);

}
