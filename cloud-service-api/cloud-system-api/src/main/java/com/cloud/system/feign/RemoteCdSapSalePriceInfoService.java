package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.feign.factory.RemoteCdSapSalePriceInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户 Feign服务层
 *
 * @author cs
 * @date 2020-06-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdSapSalePriceInfoFallbackFactory.class)
public interface RemoteCdSapSalePriceInfoService {
    /**
     * 根据Example条件查询列表
     * @param materialCode
     * @param beginDate
     * @param endDate
     * @return List<CdSapSalePrice>
     */
    @GetMapping(value = "salePrice/findByMaterialCode")
    List<CdSapSalePrice> findByMaterialCode(@RequestParam(value = "materialCode") String materialCode,
                                       @RequestParam(value = "beginDate") String beginDate,
                                       @RequestParam(value = "endDate") String endDate);


}
