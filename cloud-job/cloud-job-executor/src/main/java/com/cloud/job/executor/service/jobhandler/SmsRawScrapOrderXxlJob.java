package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteSmsRawScrapOrderService;
import com.cloud.settle.feign.RemoteSmsScrapOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 报废相关定时任务
 * @Author cs
 * @Date 2020-05-26
 */
@Component
public class SmsRawScrapOrderXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsRawScrapOrderXxlJob.class);

    @Autowired
    private RemoteSmsRawScrapOrderService remoteSmsRawScrapOrderService;

    /**
     * 每天上午8:35 更新原材料报废价格
     * 每天上午8:35开始 0 35 8 * * ?
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("rawScrapOrderUpdatePriceHandler")
    public ReturnT<String> rawScrapOrderUpdatePriceHandler(String param) {
        logger.info("更新原材料报废表价格开始");
        R r=remoteSmsRawScrapOrderService.updateRawScrapJob();
        XxlJobLogger.log(StrUtil.format("更新原材料报废表价格结果：{}",r.toString()));
        logger.info(StrUtil.format("更新原材料报废表价格结果：{}",r.toString()));
        logger.info("更新原材料报废表价格结束");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }
}
