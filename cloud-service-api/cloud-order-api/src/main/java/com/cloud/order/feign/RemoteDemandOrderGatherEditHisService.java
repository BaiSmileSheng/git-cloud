package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteDemandOrderGatherEditHisFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 滚动计划需求操作  Feign服务层
 *
 * @author cs
 * @date 2020-08-18
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteDemandOrderGatherEditHisFallbackFactory.class)
public interface RemoteDemandOrderGatherEditHisService {

    /**
     * 查询 滚动计划需求操作
     * @param id
     * @return
     */
    @GetMapping("demandOrderGatherEditHis/get")
    R get(@RequestParam("id") Long id);
}
