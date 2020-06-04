package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdBom;
import com.cloud.system.feign.factory.RemoteBomFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 数据BOM Feign服务层
 *
 * @author cs
 * @date 2020-06-01
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteBomFallbackFactory.class)
public interface RemoteBomService {
    /**
     * 根据成品物料号、原材料物料号确定一条数据
     * @param productMaterialCode
     * @param rawMaterialCode
     * @return
     */
    @GetMapping("bom/listByProductAndMaterial")
    CdBom listByProductAndMaterial(@RequestParam("productMaterialCode") String productMaterialCode, @RequestParam("rawMaterialCode") String rawMaterialCode);

    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R
     */
    @PostMapping("bom/checkBomNum")
    R checkBomNum(@RequestParam(value = "productMaterialCode") String rawMaterialCode,@RequestParam(value = "rawMaterialCode") String productMaterialCode, @RequestParam(value = "applyNum") int applyNum);
}
