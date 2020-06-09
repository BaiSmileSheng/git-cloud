package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.domain.webServicePO.BaseClaimResponse;
import com.cloud.system.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import com.cloud.system.feign.factory.RemoteBaseMultiItemClaimFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 报账单创建接口 Feign服务层
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteBaseMultiItemClaimFallbackFactory.class)
public interface RemoteBaseMultiItemClaimService {

    /**
     * 单据创建接口（支持多明细）
     */
    @PostMapping("createMultiItemClaim")
    BaseClaimResponse createMultiItemClaim(@RequestBody BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest);
}
