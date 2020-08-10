package com.cloud.job.executor.service.jobhandler;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductionOrderAnalysisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 待排产订单分析
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/17
 */
@Component
public class OmsProductionOrderAnalysisXxJob {
    @Autowired
    private RemoteProductionOrderAnalysisService remoteProductionOrderAnalysisService;
    /**
     * Description:  待排产订单分析汇总定时任务 每小时的15分、45分钟执行
     * Param: [param]
     * return: com.xxl.job.core.biz.model.ReturnT<java.lang.String>
     * Author: ltq
     * Date: 2020/6/17
     */
    @XxlJob("productionOrderAnalysisHandler")
    public ReturnT<String> productionOrderAnalysisHandler(String param) throws Exception {
        XxlJobLogger.log("=======待排产订单分析汇总定时任务开始======");
        R r = remoteProductionOrderAnalysisService.productionOrderAnalysisGatherJob();
        if (!r.isSuccess()) {
            XxlJobLogger.log("待排产订单分析汇总定时任务执行失败，原因{}：" + r.get("msg"));
            return ReturnT.FAIL;
        }
        XxlJobLogger.log("=======待排产订单分析汇总定时任务结束======");
        return ReturnT.SUCCESS;
    }
}
