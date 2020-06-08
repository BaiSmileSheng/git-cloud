package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteDelaysDeliveryService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延期索赔相关定时任务
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Component
public class SmsDelaysDeliveryXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsDelaysDeliveryXxlJob.class);

    @Autowired
    private RemoteDelaysDeliveryService remoteDelaysDeliveryService;

    /**
     * 定时生成延期索赔单
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("batchDelaysDeliveryHandler")
    public ReturnT<String> batchDelaysDeliveryHandler(String param) throws Exception {
        logger.info("生成延期索赔单开始");
        R r = remoteDelaysDeliveryService.batchAddDelaysDelivery();
        if(!r.isSuccess()){
            logger.error("生成延期索赔单异常:{}", JSONObject.toJSONString(r));
        }
        logger.info("生成延期索赔单结束");
        return ReturnT.SUCCESS;
    }

    /**
     * 48H超时未确认发送邮件
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("delaysDeliveryOverTimeSendMail")
    public ReturnT<String> delaysDeliveryOverTimeSendMail(String param) throws Exception {
        logger.info("延期索赔48H超时未确认发送邮件开始");
        R r = remoteDelaysDeliveryService.overTimeSendMail();
        if(!r.isSuccess()){
            logger.error("延期索赔48H超时未确认发送邮件异常:{}", JSONObject.toJSONString(r));
        }
        logger.info("延期索赔48H超时未确认发送邮件结束");
        return ReturnT.SUCCESS;
    }

    /**
     * 72H超时供应商自动确认
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("delaysDeliveryOverTimeConfim")
    public ReturnT<String> delaysDeliveryOverTimeConfim(String param) throws Exception {
        logger.info("延期索赔72H超时供应商自动确认开始");
        R r = remoteDelaysDeliveryService.overTimeConfim();
        if(!r.isSuccess()){
            logger.error("延期索赔72H超时供应商自动确认异常:{}", JSONObject.toJSONString(r));
        }
        logger.info("延期索赔72H超时供应商自动确认结束");
        return ReturnT.SUCCESS;
    }
}
