package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.system.feign.factory.RemoteCdProductOverdueFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdProductOverdueFallbackFactory.class)
public interface RemoteCdProductOverdueService {

    /**
     * Description: 根据工厂、物料号查询超期库存
     * Param: [cdProductOverdue]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    @PostMapping("productOverdue/selectOverStockByFactoryAndMaterial")
    R selectOverStockByFactoryAndMaterial(@RequestBody List<String> productMaterialCodeList);
}
