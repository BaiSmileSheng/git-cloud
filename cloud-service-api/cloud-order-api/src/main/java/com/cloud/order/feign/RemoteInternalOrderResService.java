package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteInternalOrderResServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 需求数据接入（800PR） Feign服务层
 *
 * @author cs
 * @date 2020-06-12
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteInternalOrderResServiceFallbackFactory.class)
public interface RemoteInternalOrderResService {
    /**
     * SAP800获取PR定时任务(周五)
     * @return
     */
    @GetMapping("demand/queryAndInsertDemandPRFromSap800Friday")
    R queryAndInsertDemandPRFromSap800Friday();

    /**
     * SAP800获取PR定时任务(周一)
     *
     * @return
     */
    @GetMapping("demand/queryAndInsertDemandPRFromSap800Monday")
    R queryAndInsertDemandPRFromSap800Monday();
}
