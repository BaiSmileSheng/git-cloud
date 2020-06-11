package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.feign.factory.RemoteCdSapSalePriceInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @GetMapping(value = "salePrice/findByMaterialCodeAndOraganization")
    List<CdSapSalePrice> findByMaterialCodeAndOraganization(@RequestParam(value = "materialCode") String materialCode,
                                                            @RequestParam(value = "oraganization") String oraganization,
                                       @RequestParam(value = "beginDate") String beginDate,
                                       @RequestParam(value = "endDate") String endDate);
    /**
     * 根据专用号和销售组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode+organization,CdMaterialPriceInfo>
     */
    @PostMapping("salePrice/selectPriceByInMaterialCodeAndDate")
    Map<String, CdSapSalePrice> selectPriceByInMaterialCodeAndDate(@RequestParam(value = "materialCodes") String materialCodes,
                                                                        @RequestParam(value = "beginDate") String beginDate,
                                                                        @RequestParam(value = "endDate") String endDate);

    /**
     * 根据销售组织、专用号更新数据
     * @param cdSapSalePrice
     * @return
     */
    @PostMapping("salePrice/updateByMarketingOrganizationAndMaterialCode")
    R updateByMarketingOrganizationAndMaterialCode(@RequestBody CdSapSalePrice cdSapSalePrice);

    /**
     * 新增保存成品销售价格
     */
    @PostMapping("salePrice/save")
    R addSave(@RequestBody CdSapSalePrice cdSapSalePrice);

}
