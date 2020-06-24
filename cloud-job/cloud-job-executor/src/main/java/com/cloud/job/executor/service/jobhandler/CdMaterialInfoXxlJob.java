package com.cloud.job.executor.service.jobhandler;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteMaterialService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CdMaterialInfoXxlJob {
    @Autowired
    private RemoteMaterialService remoteMaterialService;

    /**
     * @Description: 获取MDM物料主数据信息
     * @Param: [param]
     * @return: com.xxl.job.core.biz.model.ReturnT<java.lang.String>
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @XxlJob("cdMaterialInfoXxlJobHandler")
    public ReturnT<String> cdMaterialInfoXxlJobHandler(String param) {
        XxlJobLogger.log("==============执行获取MDM系统物料主数据定时任务开始============");
        try {
            XxlJobLogger.log("==============调用保存物料主数据服务开始============");
            R r = remoteMaterialService.saveMaterialInfo();
            if (!r.isSuccess()) {
                XxlJobLogger.log("==============定时任务执行异常============:"+r.get("msg"));
                return ReturnT.FAIL;
            }
            XxlJobLogger.log("==============调用保存物料主数据服务结束============");
        } catch (Exception e) {
            XxlJobLogger.log("定时任务执行异常："+e);
            return ReturnT.FAIL;
        }
        XxlJobLogger.log("==============执行获取MDM系统物料主数据定时任务结束============");
        return ReturnT.SUCCESS;
    }

    /**
     * @Description: 获取UPH数据
     * @Param: [param]
     * @return: com.xxl.job.core.biz.model.ReturnT<java.lang.String>
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @XxlJob("cdMaterialUphXxlJobHandler")
    public ReturnT<String> cdMaterialUphXxlJobHandler(String param) throws Exception {
        XxlJobLogger.log("=================获取UPH数据定时任务开始==================");
        R r = remoteMaterialService.updateUphBySap();
        if (!r.isSuccess()) {
            XxlJobLogger.log("==============获取UPH数据失败=============");
            return ReturnT.FAIL;
        }
        XxlJobLogger.log("=================获取UPH数据定时任务结束==================");
        return ReturnT.SUCCESS;
    }
}
