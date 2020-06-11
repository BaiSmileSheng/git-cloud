package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
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
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("synPriceHandler")
    public ReturnT<String> synPriceHandler(String param) throws Exception {
        logger.info("加工费/原材料价格同步开始");
        R r = remoteCdMaterialPriceInfoService.synPrice();
        if(!r.isSuccess()){
            logger.error("加工费/原材料价格同步异常:{}", JSONObject.toJSONString(r));
        }
        logger.info("原材料价格同步结束");
        return ReturnT.SUCCESS;
    }
}
