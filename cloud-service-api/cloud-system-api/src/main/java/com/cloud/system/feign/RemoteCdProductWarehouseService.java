package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.feign.factory.RemoteCdProductWarehouseFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdProductWarehouseFallbackFactory.class)
public interface RemoteCdProductWarehouseService {
    /**
     * Description:  查询库位库存
     * Param: [cdProductWarehouse]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    @PostMapping("productWarehouse/queryOneByExample")
    R queryOneByExample(@RequestBody CdProductWarehouse cdProductWarehouse);
    /**
     * Description:  查询库位库存List
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/17
     */
    @PostMapping("productWarehouse/queryByList")
    R queryByList(@RequestBody List<CdProductWarehouse> list);
}
