package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteProductStatementService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * T-1交付考核报表定时任务
 *
 * @Author lihongxia
 * @Date 2020-08-07
 */
@Component
@Slf4j
public class OmsProductStatementXxlJob {

    @Autowired
    private RemoteProductStatementService remoteProductStatementService;


    /**
     * 定时任务生成T-1交付考核报表
     * 0 30 0 * * ?(目的是取00:10的库存)
     */
    @XxlJob("timeAddSaveProductStatement")
    public ReturnT<String> timeAddSaveProductStatement(String param) {
        XxlJobLogger.log("--------------定时任务生成T-1交付考核报表开始----------");
        R r=remoteProductStatementService.timeAddSave();
        XxlJobLogger.log("--------------定时任务生成T-1交付考核报表结果结束------------");
        XxlJobLogger.log(StrUtil.format("定时任务生成T-1交付考核报表结果：{}",r.toString()));
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

}
