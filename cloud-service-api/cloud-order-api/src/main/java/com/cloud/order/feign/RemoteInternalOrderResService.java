package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteInternalOrderResServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    /**
     * 每天执行一次
     * 获取PO接口定时任务
     * @return
     */
    @PostMapping("demand/timeInsertFromSAP")
    R timeInsertFromSAP();
}
