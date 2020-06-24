package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductionOrderAnalysisService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Description:  待排产订单分析
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/17
 */
@Component
@Slf4j
public class RemoteProductionOrderAnalysisFallbackFactory implements FallbackFactory<RemoteProductionOrderAnalysisService> {
    @Override
    public RemoteProductionOrderAnalysisService create(Throwable throwable) {
        return new RemoteProductionOrderAnalysisService(){
            /**
             * Description: 待排产订单分析汇总定时任务
             * Param: []
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/17
             */
            @Override
            public R productionOrderAnalysisGatherJob() {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
