package com.cloud.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteMaterialExtendInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 物料扩展信息 相关定时任务
 *
 * @Author Lihongxia
 * @Date 2020-06-16
 */
@Component
public class CdMaterialExtendInfoXxlJob {

    @Autowired
    private RemoteMaterialExtendInfoService remoteMaterialExtendInfoService;


    /**
     * 定时任务传输成品物料接口
     * 要在定时同步原材料库存之前
     * 每日凌晨执行一次  0 0 0 * * ?
     *
     * @param param
     * @return 成功
     * @throws Exception
     */
    @XxlJob("timeSycMaterialCode")
    public ReturnT<String> timeSycMaterialCode(String param) throws Exception {
        XxlJobLogger.log("定时任务传输成品物料开始");
        R r = remoteMaterialExtendInfoService.timeSycMaterialCode();
        if (!r.isSuccess()) {
            XxlJobLogger.log("定时任务传输成品物料异常 :{}", JSONObject.toJSONString(r));
        }
        XxlJobLogger.log("定时任务传输成品物料结束");
        return ReturnT.SUCCESS;
    }

}
