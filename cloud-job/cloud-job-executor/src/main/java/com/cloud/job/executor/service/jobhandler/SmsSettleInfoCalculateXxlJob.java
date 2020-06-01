package com.cloud.job.executor.service.jobhandler;

import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.enums.SettleInfoOrderStatusEnum;
import com.cloud.settle.feign.RemoteSettleInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Component
public class SmsSettleInfoCalculateXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsSettleInfoCalculateXxlJob.class);

    @Autowired
    private RemoteSettleInfoService remoteSettleInfoService;

    @XxlJob("smsSettleInfoCalculateHandler")
    public ReturnT<String> smsSettleInfoCalculateHandler(String param) throws Exception {
        logger.info("加工费生成开始");
        remoteSettleInfoService.smsSettleInfoCalculate();
        logger.info("加工费生成结束");
        return ReturnT.SUCCESS;
    }

}
