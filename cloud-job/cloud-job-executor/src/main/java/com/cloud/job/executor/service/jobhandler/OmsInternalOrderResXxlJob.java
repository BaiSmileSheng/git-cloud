package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteInternalOrderResService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * SAP800获取PR数据定时任务
 *
 * @Author cs
 * @Date 2020-06-12
 */
@Component
@Slf4j
public class OmsInternalOrderResXxlJob {

    @Autowired
    private RemoteInternalOrderResService remoteInternalOrderResService;

    /**
     * SAP800获取PR数据定时任务
     * 每周五凌晨五点半  0 30 5 ? * FRI
     *
     * @return
     */
    @XxlJob("SAP800PRFridayCalculateHandler")
    public ReturnT<String> SAP800PRFridayCalculateHandler(String param) {
        log.info("--------------周五SAP800获取PR数据定时任务开始----------");
        R r=remoteInternalOrderResService.queryAndInsertDemandPRFromSap800Friday();
        XxlJobLogger.log(StrUtil.format("周五SAP800获取PR数据结果：{}",r.toString()));
        log.info(StrUtil.format("周五SAP800获取PR数据结果：{}",r.toString()));
        log.info("--------------周五SAP800获取PR数据定时任务结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

    /**
     * SAP800更新PR数据定时任务
     * 每周一凌晨五点半  0 30 5 ? * MON
     *
     * @return
     */
    @XxlJob("SAP800PRMondayCalculateHandler")
    public ReturnT<String> SAP800PRMondayCalculateHandler(String param) {
        log.info("--------------周一SAP800获取PR数据定时任务开始----------");
        R r=remoteInternalOrderResService.queryAndInsertDemandPRFromSap800Monday();
        XxlJobLogger.log(StrUtil.format("周一SAP800获取PR数据结果：{}",r.toString()));
        log.info(StrUtil.format("周一SAP800获取PR数据结果：{}",r.toString()));
        log.info("--------------周一SAP800获取PR数据定时任务结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

    /**
     * 定时任务获取PO数据
     * 每天一点  0 0 1 * * ?
     *
     * @return
     */
    @XxlJob("timeInsertFromSAP")
    public ReturnT<String> timeInsertFromSAP(String param) {
        log.info("--------------定时任务获取PO数据定时任务开始----------");
        R r=remoteInternalOrderResService.timeInsertFromSAP();
        XxlJobLogger.log(StrUtil.format("定时任务获取PO数据 数据结果：{}",r.toString()));
        log.info(StrUtil.format("定时任务获取PO数据结果：{}",r.toString()));
        log.info("--------------定时任务获取PO数据结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }
}
