package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.RemoteSmsSupplementaryOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 物耗相关定时任务
 * @Author cs
 * @Date 2020-05-26
 */
@Component
public class SmsSupplementaryOrderCalculateXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsSupplementaryOrderCalculateXxlJob.class);

    @Autowired
    private RemoteSmsSupplementaryOrderService remoteSmsSupplementaryOrderService;

    /**
     * 从原材料价格表中取得价格更新到物耗表 更新上个月的
     * 每月一号一点开始 0 0 1 1 * ?
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("supplementaryOrderUpdatePriceCalculateHandler")
    public ReturnT<String> supplementaryOrderUpdatePriceCalculateHandler(String param) {
        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");//计算月份：上个月
        logger.info("更新物耗表价格生成开始");
        R r=remoteSmsSupplementaryOrderService.updatePriceEveryMonth(lastMonth);
        XxlJobLogger.log(StrUtil.format("更新物耗表价格结果：{}",r.toString()));
        logger.info(StrUtil.format("更新物耗表价格结果：{}",r.toString()));
        logger.info("更新物耗表价格生成结束");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }

}
