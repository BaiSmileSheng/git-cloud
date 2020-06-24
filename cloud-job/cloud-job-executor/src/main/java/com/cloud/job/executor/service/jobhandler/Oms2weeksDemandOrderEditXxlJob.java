package com.cloud.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.order.feign.Remote2weeksDemandOrderEditService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 2周需求定时任务
 *
 * @Author cs
 * @Date 2020-06-12
 */
@Component
@Slf4j
public class Oms2weeksDemandOrderEditXxlJob {

    @Autowired
    private Remote2weeksDemandOrderEditService remote2weeksDemandOrderEditService;

    /**
     * 2周需求从SAP获取计划订单号
     * 每隔两小时执行一次
     * @return
     */
    @XxlJob("queryPlanOrderCodeFromSap601")
    public ReturnT<String> queryPlanOrderCodeFromSap601(String param) {
        log.info("--------------2周需求从SAP获取计划订单号定时任务开始----------");
        R r=remote2weeksDemandOrderEditService.queryPlanOrderCodeFromSap601();
        XxlJobLogger.log(StrUtil.format("2周需求从SAP获取计划订单号数据结果：{}",r.toString()));
        log.info(StrUtil.format("2周需求从SAP获取计划订单号数据结果：{}",r.toString()));
        log.info("--------------2周需求从SAP获取计划订单号定时任务结束------------");
        if (r.isSuccess()) {
            return ReturnT.SUCCESS;
        }else{
            return ReturnT.FAIL;
        }
    }
}
