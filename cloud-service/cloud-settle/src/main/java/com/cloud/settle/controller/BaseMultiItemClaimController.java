package com.cloud.settle.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.controller.BaseController;
import com.cloud.settle.domain.webServicePO.BaseClaimResponse;
import com.cloud.settle.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import com.cloud.settle.service.IBaseMutilItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报账单创建接口  提供者
 *
 * @author Lihongxia
 * @date 2020-06-01
 */
@RestController
@RequestMapping("baseMultiItemClaimController")
@Api(tags = "报账单创建接口  提供者")
public class BaseMultiItemClaimController extends BaseController {

    @Autowired
    private IBaseMutilItemService iBaseMutilItemService;

    /**
     * 单据创建接口（支持多明细）
     * @param baseMultiItemClaimSaveRequest 报账单信息
     * @return
     * @throws Exception
     */
    @PostMapping("createMultiItemClaim")
    @ApiOperation(value = "单据创建接口（支持多明细） ", response = BaseClaimResponse.class)
    public BaseClaimResponse createMultiItemClaim(@RequestBody BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest){

        logger.info("单据创建接口（支持多明细）req:{}", JSONObject.toJSONString(baseMultiItemClaimSaveRequest));
        BaseClaimResponse result = iBaseMutilItemService.createMultiItemClaim(baseMultiItemClaimSaveRequest);
        logger.info("单据创建接口（支持多明细）res:{}", JSONObject.toJSONString(result));
        return result;
    }

}
