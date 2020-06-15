package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteDemandOrderGatherService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 汇总需求数据定时任务
 *
 * @Author cs
 * @Date 2020-06-12
 */
@Component
@Slf4j
public class OmsDemandOrderGatherXxlJob {

    @Autowired
    private RemoteDemandOrderGatherService remoteDemandOrderGatherService;

    /**
     * 周五需求数据汇总
     * 每周五凌晨四点  0 0 4 ? * FRI
     *
     * @return
     */
    @XxlJob("gatherDemandOrderFriday")
    public ReturnT<String> gatherDemandOrderFriday(String param) {
        log.info("--------------周五需求数据汇总定时任务开始----------");
        R r=remoteDemandOrderGatherService.gatherDemandOrderFriday();
        XxlJobLogger.log(StrUtil.format("周五需求数据汇总数据结果：{}",r.toString()));
        log.info(StrUtil.format("周五需求数据汇总数据结果：{}",r.toString()));
        log.info("--------------周五需求数据汇总定时任务结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

    /**
     * 周一需求数据汇总
     * 每周一凌晨四点  0 0 4 ? * MON
     *
     * @return
     */
    @XxlJob("gatherDemandOrderMonday")
    public ReturnT<String> gatherDemandOrderMonday(String param) {
        log.info("--------------周一需求数据汇总定时任务开始----------");
        R r=remoteDemandOrderGatherService.gatherDemandOrderMonday();
        XxlJobLogger.log(StrUtil.format("周一需求数据汇总数据结果：{}",r.toString()));
        log.info(StrUtil.format("周一需求数据汇总数据结果：{}",r.toString()));
        log.info("--------------周一需求数据汇总定时任务结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }
}
