package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteProductStockService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 成品库存主表相关定时任务
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Component
public class SystemCDProductStockXxlJob {

    @Autowired
    private RemoteProductStockService remoteProductStockService;


    /**
     * 每天定时同步成品库存信息
     * 每30分钟执行一次  0 0/30 * * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("timeSycProductStock")
    public ReturnT<String> timeSycProductStock(String param){
        XxlJobLogger.log("获取SAP成品库存信息开始");
        R r = remoteProductStockService.timeSycProductStock();
        XxlJobLogger.log("获取SAP成品库存信息结束");
        XxlJobLogger.log("获取SAP成品库存信息异常 :{}",JSONObject.toJSONString(r));
        if(!r.isSuccess()){
            return ReturnT.FAIL;
        }else {
            return ReturnT.SUCCESS;
        }
    }

}
