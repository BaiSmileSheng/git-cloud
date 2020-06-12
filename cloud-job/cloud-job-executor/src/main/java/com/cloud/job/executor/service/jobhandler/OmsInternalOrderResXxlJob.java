package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteInternalOrderResService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


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
     * 每周五凌晨三点  0 0 3 ? * FRI
     *
     * @return
     */
    @XxlJob("SAP800PRFridayCalculateHandler")
    public ReturnT<String> SAP800PRFridayCalculateHandler(String param) {
        log.info("--------------周五SAP800获取PR数据定时任务开始----------");
        //开始：这个周天
        Date startDate = DateUtil.endOfWeek(DateUtil.date());
        //结束：往后推90天
        Date endDate = DateUtil.offset(startDate, DateField.DAY_OF_YEAR, 90);
        R r=remoteInternalOrderResService.queryAndInsertDemandPRFromSap800(startDate,endDate);
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
     * 每周一凌晨三点  0 0 3 ? * MON
     *
     * @return
     */
    @XxlJob("SAP800PRMondayCalculateHandler")
    public ReturnT<String> SAP800PRMondayCalculateHandler(String param) {
        log.info("--------------SAP800获取PR数据定时任务开始----------");
        //开始：上个周天
        Date startDate = DateUtil.endOfWeek(DateUtil.lastWeek());
        //结束：往后推90天
        Date endDate = DateUtil.offset(startDate, DateField.DAY_OF_YEAR, 90);
        R r=remoteInternalOrderResService.queryAndInsertDemandPRFromSap800(startDate,endDate);
        XxlJobLogger.log(StrUtil.format("周一SAP800获取PR数据结果：{}",r.toString()));
        log.info(StrUtil.format("周一SAP800获取PR数据结果：{}",r.toString()));
        log.info("--------------周一SAP800获取PR数据定时任务结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }
}
