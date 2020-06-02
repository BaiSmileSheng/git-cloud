package com.cloud.activiti.listener;

import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.feign.RemoteQualityOrderService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 质量审核结束监听
 * @Author Lihongxia
 * @Date 2020-05-29
 */
@Slf4j
@Component
public class SmsQualityOrderCompletedListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String eventName = delegateExecution.getEventName();
        // complete:在任务完成后，且被从运行时数据（runtime data）中删除前触发。
        if ("end".equals(eventName)) {
            //获取BEAN
            RemoteQualityOrderService remoteQualityOrderService = ApplicationContextUtil.getBean(RemoteQualityOrderService.class);
            IBizBusinessService bizBusinessService = ApplicationContextUtil.getBean(IBizBusinessService.class);

            Set<String> variableNames = delegateExecution.getVariableNames();
            //处理业务逻辑
            BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(delegateExecution.getBusinessKey());
            SmsQualityOrder smsQualityOrder = remoteQualityOrderService.get(Long.valueOf(bizBusiness.getTableId()));
            for (String key : variableNames) {
                Object value = delegateExecution.getVariable(key);
                log.info("参数{}={}", key, value);
            }
            //result为2时表示通过  为3时表示驳回
            if ("2".equals(delegateExecution.getVariable("result").toString())) {
                //小微主审核同意状态改为待结算,结算金额改为0
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_11.getCode());
                smsQualityOrder.setSettleFee(BigDecimal.ZERO);
            } else {
                //驳回状态改为供应商待确认(驳回)
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_7.getCode());
            }
            remoteQualityOrderService.editSave(smsQualityOrder);
        }
    }
}
