package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteInternalOrderResServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * 需求数据接入（800PR） Feign服务层
 *
 * @author cs
 * @date 2020-06-12
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteInternalOrderResServiceFallbackFactory.class)
public interface RemoteInternalOrderResService {
    /**
     * 根据时间从800获取PR
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("demand/queryAndInsertDemandPRFromSap800")
    R queryAndInsertDemandPRFromSap800(@RequestParam("startDate") Date startDate, @RequestParam("endDate")Date endDate);
}
