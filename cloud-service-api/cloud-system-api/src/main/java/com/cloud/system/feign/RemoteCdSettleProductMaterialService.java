package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.feign.factory.RemoteCdSettleProductMaterialFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 物料号和加工费号对应关系 Feign服务层
 *
 * @author cs
 * @date 2020-06-01
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdSettleProductMaterialFallbackFactory.class)
public interface RemoteCdSettleProductMaterialService {

    /**
     * 根据成品物料编码 查物料号和加工费号对应关系
     * @param productMaterialCode 成品物料编码
     * @param rawMaterialCode 加工费号
     * @return 物料号和加工费号对应关系列表
     */
    @GetMapping("settleProducMaterial/listByCode")
    R listByCode(@RequestParam(value = "productMaterialCode") String productMaterialCode,
                 @RequestParam(value = "rawMaterialCode",required = false) String rawMaterialCode);

    /**
     * 根据成品专用号和委外方式查加工费号
     * @param productMaterialCode
     * @param outsourceWay
     * @return
     */
    @GetMapping("settleProducMaterial/selectOne")
    R selectOne(@RequestParam(value = "productMaterialCode") String productMaterialCode,
                @RequestParam("outsourceWay") String outsourceWay);
}
