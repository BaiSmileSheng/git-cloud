package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteQualityOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 质量索赔相关定时任务
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Component
public class SmsQualityOrderXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsQualityOrderXxlJob.class);

    @Autowired
    private RemoteQualityOrderService remoteQualityOrderService;


    /**
     * 48H超时未确认发送邮件
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("qualityOrderOverTimeSendMail")
    public ReturnT<String> qualityOrderOverTimeSendMail(String param) throws Exception {
        logger.info("质量索赔48H超时未确认发送邮件开始");
        R r = remoteQualityOrderService.overTimeSendMail();
        if(!r.isSuccess()){
            logger.error("质量索赔48H超时未确认发送邮件异常:{}", JSONObject.toJSONString(r));
        }
        logger.info("质量索赔48H超时未确认发送邮件结束");
        return ReturnT.SUCCESS;
    }

    /**
     * 72H超时供应商自动确认
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("qualityOrderOverTimeConfim")
    public ReturnT<String> qualityOrderOverTimeConfim(String param) throws Exception {
        logger.info("质量索赔72H超时供应商自动确认开始");
        R r = remoteQualityOrderService.overTimeConfim();
        if(!r.isSuccess()){
            logger.error("质量索赔72H超时供应商自动确认异常:{}", JSONObject.toJSONString(r));
        }
        logger.info("质量索赔72H超时供应商自动确认结束");
        return ReturnT.SUCCESS;
    }
}
