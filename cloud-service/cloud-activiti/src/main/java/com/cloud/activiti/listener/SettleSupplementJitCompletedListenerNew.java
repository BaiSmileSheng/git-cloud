package com.cloud.activiti.listener;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsSupplementaryOrderService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.Constants;
import com.cloud.common.utils.ServletUtils;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 节点任务完成监听类,画流程图时需配置  样例
 *
 * @auther: cs
 */

@Slf4j
@Component
public class SettleSupplementJitCompletedListenerNew implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        String auditUserIdVal= ServletUtils.getRequest().getHeader(Constants.CURRENT_ID);
        String eventName = delegateTask.getEventName();
        // complete:在任务完成后，且被从运行时数据（runtime data）中删除前触发。
        if ("end".equals(eventName)) {
            //获取BEAN
            IBizBusinessService bizBusinessService = ApplicationContextUtil.getBean(IBizBusinessService.class);
            IActSmsSupplementaryOrderService actSmsSupplementaryOrderService = ApplicationContextUtil.getBean(IActSmsSupplementaryOrderService.class);
            String exId = delegateTask.getExecutionId();
            ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
            RuntimeService runtimeService = engine.getRuntimeService();

            Set<String> variableNames = delegateTask.getExecution().getVariableNames();
            for (String key : variableNames) {
                Object value = delegateTask.getExecution().getVariable(key);
                log.info("参数{}={}", key, value);
            }
            //处理业务逻辑
            BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(delegateTask.getExecution().getBusinessKey());
            String resultVar = delegateTask.getExecution().getVariable("result").toString();

            Integer result = Integer.parseInt(resultVar);
            BizAudit bizAudit = new BizAudit();
            bizAudit.setTaskId(delegateTask.getId());
            bizAudit.setResult(result);
            bizAudit.setProcDefKey(bizBusiness.getProcDefKey());
            bizAudit.setProcName(bizBusiness.getProcName());
            bizAudit.setApplyer(bizBusiness.getApplyer());
            bizAudit.setProcInstId(bizBusiness.getProcInstId());
            bizAudit.setBusinessKey(bizBusiness.getId());

            actSmsSupplementaryOrderService.audit(bizAudit, Long.valueOf(auditUserIdVal));
        }
    }
}
