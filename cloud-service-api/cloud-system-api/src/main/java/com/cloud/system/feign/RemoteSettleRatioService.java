package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.domain.entity.CdSettleRatio;
import com.cloud.system.feign.factory.RemoteSettleRatioFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 结算索赔系数Feign服务层
 *
 * @author Lihongxia
 * @date 2020-06-04
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSettleRatioFallbackFactory.class)
public interface RemoteSettleRatioService {

    /**
     * 查询结算索赔系数
     * @param claimType 索赔类型
     * @return 结算索赔系数
     */
    @GetMapping("settleRatio/selectByClaimType")
    CdSettleRatio selectByClaimType(@RequestParam("claimType") String claimType);

}
