package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteClaimOtherService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 其他索赔相关定时任务
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Component
public class SmsChaimOtherXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsChaimOtherXxlJob.class);

    @Autowired
    private RemoteClaimOtherService remoteClaimOtherService;


    /**
     * 48H超时未确认发送邮件
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("chaimOtherOverTimeSendMail")
    public ReturnT<String> chaimOtherOverTimeSendMail(String param){
        XxlJobLogger.log("其他索赔48H超时未确认发送邮件开始");
        R r = remoteClaimOtherService.overTimeSendMail();
        XxlJobLogger.log("其他索赔48H超时未确认发送邮件结束");
        XxlJobLogger.log("其他索赔48H超时未确认发送邮件异常:{}", JSONObject.toJSONString(r));
        if(!r.isSuccess()){
            return ReturnT.FAIL;
        }else {
            return ReturnT.SUCCESS;
        }
    }

    /**
     * 72H超时供应商自动确认
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("chaimOtherOverTimeConfim")
    public ReturnT<String> chaimOtherOverTimeConfim(String param){
        XxlJobLogger.log("其他索赔72H超时供应商自动确认开始");
        R r = remoteClaimOtherService.overTimeConfim();
        XxlJobLogger.log("其他索赔72H超时供应商自动确认结束");
        XxlJobLogger.log("其他索赔72H超时供应商自动确认异常:{}", JSONObject.toJSONString(r));
        if(!r.isSuccess()){
            return ReturnT.FAIL;
        }else {
            return ReturnT.SUCCESS;
        }
    }
}
