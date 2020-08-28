package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteSettleInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 加工费相关定时任务
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Component
public class SmsSettleInfoCalculateXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsSettleInfoCalculateXxlJob.class);

    @Autowired
    private RemoteSettleInfoService remoteSettleInfoService;

    /**
     * 定时生成加工费
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("smsSettleInfoCalculateHandler")
    public ReturnT<String> smsSettleInfoCalculateHandler(String param){
        XxlJobLogger.log("加工费生成开始");
        R r = remoteSettleInfoService.smsSettleInfoCalculate();
        XxlJobLogger.log("加工费生成结束");
        XxlJobLogger.log("加工费生成异常:{}", JSONObject.toJSONString(r));
        if(!r.isSuccess()){
            return ReturnT.FAIL;
        }else {
            return ReturnT.SUCCESS;
        }
    }

}
