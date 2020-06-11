package com.cloud.settle.service;

import com.cloud.settle.domain.webServicePO.BaseClaimResponse;
import com.cloud.settle.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 报账单创建接口
 *
 * @author Lihongxia
 * @date 2020-06-09
 */
public interface IBaseMutilItemService{
    /**
     * 单据创建接口（支持多明细）
     * @param baseMultiItemClaimSaveRequest 报账单信息
     * @return
     */
    BaseClaimResponse createMultiItemClaim(BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest);
}
