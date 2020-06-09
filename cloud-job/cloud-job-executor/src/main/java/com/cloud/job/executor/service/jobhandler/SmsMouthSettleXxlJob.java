package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteMouthSettleService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 月度结算定时任务
 * @Author cs
 * @Date 2020-06-05
 */
@Component
@Slf4j
public class SmsMouthSettleXxlJob {

    @Autowired
    private RemoteMouthSettleService remoteMouthSettleService;

    /**
     * 月度结算
     * 每月1号 五点执行  0 0 5 1 * ? *
     * @return
     */
    @XxlJob("smsMouthSettleCalculateHandler")
    public ReturnT<String> smsMouthSettleCalculateHandler(String param) {
        log.info("--------------月度结算定时任务开始----------");
        R r=remoteMouthSettleService.countMonthSettle();
        XxlJobLogger.log(StrUtil.format("月度结算审批结果：{}",r.toString()));
        log.info(StrUtil.format("月度结算审批结果：{}",r.toString()));
        log.info("--------------月度结算定时任务结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

}
