package com.cloud.job.executor.service.jobhandler;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductDifferenceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OmsProductDifferenceXxlJob {
    @Autowired
    private RemoteProductDifferenceService remoteProductDifferenceService;
    @XxlJob("timeProductDiffTaskHandler")
    public ReturnT<String> timeProductDiffTaskHandler(String param) throws Exception {
        XxlJobLogger.log("=======生成外单排产差异报表定时任务开始======");
        R r = remoteProductDifferenceService.timeProductDiffTask();
        if (!r.isSuccess()) {
            XxlJobLogger.log("生成外单排产差异报表定时任务失败，原因{}：" + r.get("msg"));
            return ReturnT.FAIL;
        }
        XxlJobLogger.log("=========外单排产差异报表"+r.get("msg")+"===========");
        XxlJobLogger.log("=======生成外单排产差异报表定时任务结束======");
        return ReturnT.SUCCESS;
    }
}
