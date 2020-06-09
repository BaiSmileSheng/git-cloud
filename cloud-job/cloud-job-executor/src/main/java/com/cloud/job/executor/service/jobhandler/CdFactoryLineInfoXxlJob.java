package com.cloud.job.executor.service.jobhandler;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @Description: 获取工厂线体关系数据定时任务
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/8
 */
@Component
public class CdFactoryLineInfoXxlJob {
    @Autowired
    private RemoteFactoryLineInfoService remoteFactoryLineInfoService;
    /**
     * @Description:  定时任务获取工厂线体关系数据，并保存
     * @Param: [param]
     * @return: com.xxl.job.core.biz.model.ReturnT<java.lang.String>
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @XxlJob("cdFactoryLineInfoXxlJobHandler")
    public ReturnT<String> cdFactoryLineInfoXxlJobHandler(String param) throws  Exception{
        XxlJobLogger.log(" ================获取工厂线体关系数据定时任务开始==================");
        R r = remoteFactoryLineInfoService.saveFactoryLineInfo();
        if (!r.isSuccess()) {
            XxlJobLogger.log("定时任务获取工厂线体关系数据失败,原因："+r.get("msg"));
            return ReturnT.FAIL;
        }
        XxlJobLogger.log(" ================获取工厂线体关系数据定时任务开始==================");
        return ReturnT.SUCCESS;
    }
}
