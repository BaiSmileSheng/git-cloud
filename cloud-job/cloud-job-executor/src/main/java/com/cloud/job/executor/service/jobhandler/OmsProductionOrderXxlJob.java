package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteOmsRealOrderService;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 排产订单定时任务
 *
 * @Author lihongxia
 * @Date 2020-07-02
 */
@Component
@Slf4j
public class OmsProductionOrderXxlJob {

    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;


    /**
     * 定时任务SAP获取订单号
     * 0 0 2 * * ?
     */
    @XxlJob("timeSAPGetProductOrderCode")
    public ReturnT<String> timeSAPGetProductOrderCode(String param) {
        log.info("--------------定时任务SAP获取订单号开始----------");
        R r=remoteProductionOrderService.timeSAPGetProductOrderCode();
        XxlJobLogger.log(StrUtil.format("定时任务SAP获取订单号结果：{}",r.toString()));
        log.info(StrUtil.format("定时任务SAP获取订单号结果：{}",r.toString()));
        log.info("--------------定时任务SAP获取订单号结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

}
