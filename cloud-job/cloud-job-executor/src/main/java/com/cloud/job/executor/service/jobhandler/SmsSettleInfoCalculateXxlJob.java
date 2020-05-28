package com.cloud.job.executor.service.jobhandler;

import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.enums.SettleInfoOrderStatusEnum;
import com.cloud.settle.feign.RemoteSettleInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Component
public class SmsSettleInfoCalculateXxlJob {

    private static Logger logger = LoggerFactory.getLogger(SmsSettleInfoCalculateXxlJob.class);

    @Autowired
    private RemoteSettleInfoService remoteSettleInfoService;

    @XxlJob("smsSettleInfoCalculateHandler")
    public ReturnT<String> smsSettleInfoCalculateHandler(String param) throws Exception {

        logger.info("加工费生成开始");
        //1.查询已关单的加工费结算信息表--加工费结算
        SmsSettleInfo smsSettleInfoReq = new SmsSettleInfo();
        smsSettleInfoReq.setOrderStatus(SettleInfoOrderStatusEnum.ORDER_STATUS_2.getCode());
        List<SmsSettleInfo> smsSettleInfoList = remoteSettleInfoService.listByCondition(smsSettleInfoReq);
        //2.计算加工费 (订单交货数量*sap加工费单价=生产订单加工费金额 )
        //3.修改加工费结算信息表--加工费结算金额,状态为待结算
        for(SmsSettleInfo smsSettleInfo: smsSettleInfoList){
            if(null != smsSettleInfo.getMachiningPrice() && null != smsSettleInfo.getConfirmAmont() ){
                BigDecimal settlePrice = smsSettleInfo.getMachiningPrice().multiply(new BigDecimal(smsSettleInfo.getConfirmAmont()));
                smsSettleInfo.setSettlePrice(settlePrice);
                smsSettleInfo.setOrderStatus(SettleInfoOrderStatusEnum.ORDER_STATUS_3.getCode());
                remoteSettleInfoService.editSave(smsSettleInfo);
            }
        }
        logger.info("加工费生成结束");
        return ReturnT.SUCCESS;
    }

}
