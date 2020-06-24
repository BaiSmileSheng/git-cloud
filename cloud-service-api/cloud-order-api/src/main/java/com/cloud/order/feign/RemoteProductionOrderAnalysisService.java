package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.factory.RemoteProductionOrderAnalysisFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
/**
 * Description:  待排产订单分析
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/17
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteProductionOrderAnalysisFallbackFactory.class)
public interface RemoteProductionOrderAnalysisService {
    /**
     * Description: 待排产订单分析汇总定时任务
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/17
     */
    @PostMapping("analysis/productionOrderAnalysisGatherJob")
    R productionOrderAnalysisGatherJob();
}
