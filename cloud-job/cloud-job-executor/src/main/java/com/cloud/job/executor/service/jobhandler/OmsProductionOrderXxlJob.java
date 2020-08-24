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
     * 0 0/10 * * * ?
     */
    @XxlJob("timeSAPGetProductOrderCode")
    public ReturnT<String> timeSAPGetProductOrderCode(String param) {
        XxlJobLogger.log("--------------定时任务SAP获取订单号开始----------");
        R r=remoteProductionOrderService.timeSAPGetProductOrderCode();
        XxlJobLogger.log(StrUtil.format("定时任务SAP获取订单号结果：{}",r.toString()));
        XxlJobLogger.log("--------------定时任务SAP获取订单号结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

    /**
     * 定时任务生成加工结算信息
     * 0 0 0 * * ?
     */
    @XxlJob("timeInsertSettleList")
    public ReturnT<String> timeInsertSettleList(String param) {
        XxlJobLogger.log("--------------定时任务生成加工结算信息开始----------");
        R r=remoteProductionOrderService.timeInsertSettleList();
        XxlJobLogger.log(StrUtil.format("定时任务生成加工结算信息结果：{}",r.toString()));
        XxlJobLogger.log("--------------定时任务生成加工结算信息结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

    /**
     * 定时任务获取入库量
     * 0 0 0 * * ?
     */
    @XxlJob("timeGetConfirmAmont")
    public ReturnT<String> timeGetConfirmAmont(String param) {
        XxlJobLogger.log("--------------定时任务获取入库量开始----------");
        R r=remoteProductionOrderService.timeGetConfirmAmont();
        XxlJobLogger.log(StrUtil.format("定时任务获取入库量结果：{}",r.toString()));
        XxlJobLogger.log("--------------定时任务获取入库量结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

}
