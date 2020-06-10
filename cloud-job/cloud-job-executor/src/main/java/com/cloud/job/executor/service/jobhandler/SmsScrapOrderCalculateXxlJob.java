package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
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
public class SmsScrapOrderCalculateXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsScrapOrderCalculateXxlJob.class);

    @Autowired
    private RemoteSmsScrapOrderService remoteSmsScrapOrderService;

    /**
     * 从销售价格表中取得价格更新到报废表 更新上个月的
     * 每月一号一点开始 0 0 1 1 * ?
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("scrapOrderUpdatePriceCalculateHandler")
    public ReturnT<String> scrapOrderUpdatePriceCalculateHandler(String param) {
        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");//计算月份：上个月
        logger.info("更新物耗表价格生成开始");
        R r=remoteSmsScrapOrderService.updatePriceEveryMonth(lastMonth);
        XxlJobLogger.log(StrUtil.format("更新报废表价格结果：{}",r.toString()));
        logger.info(StrUtil.format("更新报废表价格结果：{}",r.toString()));
        logger.info("更新报废表价格生成结束");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

    /**
     * 定时任务更新指定月份SAP销售价格 更新上个月的
     * 每月一号0点30分开始 0 30 0 1 * ?
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("updateSAPPriceEveryMonthCalculateHandler")
    public ReturnT<String> updateSAPPriceEveryMonthCalculateHandler(String param) {
        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");//计算月份：上个月
        logger.info("更新SAP销售价格表价格生成开始");
        R r=remoteSmsScrapOrderService.updateSAPPriceEveryMonth(lastMonth);
        XxlJobLogger.log(StrUtil.format("更新SAP销售价格表价格结果：{}",r.toString()));
        logger.info(StrUtil.format("更新SAP销售价格表价格结果：{}",r.toString()));
        logger.info("更新SAP销售价格表价格生成结束");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            XxlJobLogger.log(r.get("msg").toString());
            return ReturnT.FAIL;
        }
    }

}
