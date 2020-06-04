package com.cloud.job.executor.service.jobhandler;

import com.cloud.settle.feign.RemoteDelaysDeliveryService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延期索赔相关定时任务
 * 每日凌晨执行一次  0 0 0 * * ?
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
        remoteDelaysDeliveryService.batchAddDelaysDelivery();
        logger.info("生成延期索赔单结束");
        return ReturnT.SUCCESS;
    }

}
