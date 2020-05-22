package com.cloud.activiti.listener;

import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.settle.domain.entity.SettleTestAct;
import com.cloud.settle.feign.RemoteSettleTestActService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 节点任务完成监听类,画流程图时需配置  样例
 *
 * @auther: cs
 */

@Slf4j
@Component
public class SettleTestCompletedListener implements ExecutionListener {
    private static final long serialVersionUID = 5808415173145957468L;

    // 监听任务的事件  eventName：start take end 现在只用到end
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String eventName = delegateExecution.getEventName();
        String processInstanceId = delegateExecution.getProcessInstanceId();
        // complete:在任务完成后，且被从运行时数据（runtime data）中删除前触发。
        if ("end".equals(eventName)) {
            //获取BEAN
            RemoteSettleTestActService remoteSettleTestActService = ApplicationContextUtil.getBean(RemoteSettleTestActService.class);
            IBizBusinessService bizBusinessService = ApplicationContextUtil.getBean(IBizBusinessService.class);

            Set<String> variableNames = delegateExecution.getVariableNames();
            //处理业务逻辑
            BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(delegateExecution.getBusinessKey());
            SettleTestAct settleTestAct = remoteSettleTestActService.get(Long.valueOf(bizBusiness.getTableId()));
            for (String key : variableNames) {
                Object value = delegateExecution.getVariable(key);
                log.info("参数{}={}", key, value);
            }
            //result为2时表示通过  为3时表示驳回
            if ("2".equals(delegateExecution.getVariable("result").toString())) {
                settleTestAct.setRemark("我结束了，审批同意");
            } else {
                settleTestAct.setRemark("我结束了，被驳回了");
            }
            remoteSettleTestActService.editSave(settleTestAct);
            System.out.println("-----------------------流程结束啦！！！！！！！！！！----------------------");
        }
    }
}
