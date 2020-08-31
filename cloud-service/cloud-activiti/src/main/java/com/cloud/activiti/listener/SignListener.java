package com.cloud.activiti.listener;

import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @auther cs
 * @date 2020/8/26 17:04
 * @description
 */
@Slf4j
public class SignListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("---------会签审批开始----------");
        //获取流程id
        String exId = delegateTask.getExecutionId();
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = engine.getRuntimeService();

        //获取流程参数pass，会签人员完成自己的审批任务时会添加流程参数pass，false为拒绝，true为同意
        boolean pass = (Boolean) runtimeService.getVariable(exId, "pass");
        String businessKey = runtimeService.getVariable(exId, "businessKey")+"";
        IBizBusinessService bizBusinessService = ApplicationContextUtil.getBean(IBizBusinessService.class);
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(businessKey);
        log.info("会签审批信息【订单编号：{}，流程名称：{}，当前任务节点：{}，关联表id：{}】",
                bizBusiness.getOrderNo(),bizBusiness.getProcName(),
                bizBusiness.getCurrentTask(),bizBusiness.getTableId());

        /*
         * false：有一个人拒绝，整个流程就结束了，
         * 	因为Complete condition的值为pass == false，即，当流程参数为pass时会签就结束开始下一个任务
         * 	所以，当pass == false时，直接设置下一个流程跳转需要的参数
         * true：审批人同意，同时要判断是不是所有的人都已经完成了，而不是由一个人同意该会签就结束
         * 	值得注意的是如果一个审批人完成了审批进入到该监听时nrOfCompletedInstances的值还没有更新，因此需要+1
         */
        if(pass==false){
            log.warn("----------------会签审批驳回-----------------");
            //审批驳回
            //会签结束，设置参数result为3(通过result:2  驳回result:3)
            runtimeService.setVariable(exId, "result", "3");

            bizBusiness.setCurrentTask(ActivitiConstant.END_TASK_NAME).setStatus(ActivitiConstant.STATUS_FINISH)
            .setResult(Integer.parseInt(delegateTask.getExecution().getVariable("result").toString()));
            bizBusinessService.updateBizBusiness(bizBusiness);
            //TODO:驳回业务逻辑
        }else{
            Integer complete = (Integer) runtimeService.getVariable(exId, "nrOfCompletedInstances");
            Integer all = (Integer) runtimeService.getVariable(exId, "nrOfInstances");
            Integer noComplete = (Integer) runtimeService.getVariable(exId,"nrOfActiveInstances");
            //说明都完成了并且没有人拒绝
            if((complete + 1) / all == 1){
                log.info("会签审批全部通过！！！！！！！");
                runtimeService.setVariable(exId, "result", "2");
                //TODO:会签都通过业务逻辑
            } else if (noComplete <= 0){
                //有人驳回，则整体驳回
                //会签结束，设置参数result为3(通过result:2  驳回result:3)
                runtimeService.setVariable(exId, "result", "3");
            }
        }
    }
}
