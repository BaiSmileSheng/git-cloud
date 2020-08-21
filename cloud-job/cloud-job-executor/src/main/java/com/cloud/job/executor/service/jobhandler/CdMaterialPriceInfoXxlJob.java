package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SAP成本价格相关定时任务
 * @Author Lihongxia
 * @Date 2020-06-11
 */
@Component
public class CdMaterialPriceInfoXxlJob {

    private static Logger logger = LoggerFactory.getLogger(CdMaterialPriceInfoXxlJob.class);

    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;

    /**
     * 4加工费/原材料价格同步
     * 每日凌晨1点执行一次  0 0 1 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("synPriceJGFHandler")
    public ReturnT<String> synPriceJGFHandler(String param){
        XxlJobLogger.log("加工费价格同步开始");
        R r = remoteCdMaterialPriceInfoService.synPriceJGF();
        XxlJobLogger.log("加工费价格同步结束");
        XxlJobLogger.log("加工费同步异常:{}", JSONObject.toJSONString(r));
        if(!r.isSuccess()){
            return ReturnT.FAIL;
        }else {
            return ReturnT.SUCCESS;
        }
    }

    /**
     * 4加工费/原材料价格同步
     * 每月1号执行一次  0 0 0 1 * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("synPriceYCLHandler")
    public ReturnT<String> synPriceYCLHandler(String param){
        XxlJobLogger.log("原材料价格同步开始");
        R r = remoteCdMaterialPriceInfoService.synPriceYCL();
        XxlJobLogger.log("原材料价格同步结束");
        XxlJobLogger.log("原材料价格同步异常:{}", JSONObject.toJSONString(r));
        if(!r.isSuccess()){
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
}
