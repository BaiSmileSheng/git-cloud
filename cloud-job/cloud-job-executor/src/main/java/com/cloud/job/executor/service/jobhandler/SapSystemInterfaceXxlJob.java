package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteSapSystemInterfaceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * system模块对SAP系统接口相关定时任务
 *
 * @Author Lihongxia
 * @Date 2020-06-15
 */
@Component
public class SapSystemInterfaceXxlJob {

    @Autowired
    private RemoteSapSystemInterfaceService remoteSapSystemInterfaceService;


    /**
     * 每天定时同步原材料库存
     * 要在传输物料号任务之后
     * 每日凌晨执行一次  0 0 5 * * ?
     *
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("sycRawMaterialStock")
    public ReturnT<String> sycRawMaterialStock(String param) throws Exception {
        XxlJobLogger.log("定时同步原材料库存信息开始");
        R r = remoteSapSystemInterfaceService.sycRawMaterialStock();
        if (!r.isSuccess()) {
            XxlJobLogger.log("定时同步原材料库存异常 :{}", JSONObject.toJSONString(r));
        }
        XxlJobLogger.log("定时同步原材料库存结束");
        return ReturnT.SUCCESS;
    }

    /**
     * 定时获取BOM清单数据
     * 每日凌晨执行一次  0 0 0 * * ?
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("sycBomInfo")
    public ReturnT<String> sycBomInfo(String param) throws Exception {
        XxlJobLogger.log("定时定时获取BOM清单数据开始");
        R r = remoteSapSystemInterfaceService.sycBomInfo();
        if (!r.isSuccess()) {
            XxlJobLogger.log("定时获取BOM清单异常 :{}", JSONObject.toJSONString(r));
        }
        XxlJobLogger.log("定时获取BOM清单结束");
        return ReturnT.SUCCESS;
    }
}
