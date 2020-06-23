package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteOmsRealOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 真单 定时任务
 *
 * @Author lihongxia
 * @Date 2020-06-18
 */
@Component
@Slf4j
public class OmsRealOrderXxlJob {

    @Autowired
    private RemoteOmsRealOrderService remoteOmsRealOrderService;

    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     * 0 0 2 * * ?
     *
     * @return
     */
    @XxlJob("timeCollectToOmsRealOrder")
    public ReturnT<String> timeCollectToOmsRealOrder(String param) {
        log.info("--------------定时任务每天在获取到PO信息后 进行需求汇总开始----------");
        R r=remoteOmsRealOrderService.timeCollectToOmsRealOrder();
        XxlJobLogger.log(StrUtil.format("定时任务每天在获取到PO信息后 进行需求汇总结果：{}",r.toString()));
        log.info(StrUtil.format("定时任务每天在获取到PO信息后 进行需求汇总结果：{}",r.toString()));
        log.info("--------------定时任务每天在获取到PO信息后 进行需求汇总结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

}
