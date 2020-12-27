package com.cloud.activiti.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsSupplementaryOrderService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.Constants;
import com.cloud.common.core.domain.R;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.ServletUtils;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 节点任务完成监听类,画流程图时需配置执行监听器  样例
 *
 * @auther: cs
 */

@Slf4j
@Component
public class SettleSupplementJitCompletedListener implements ExecutionListener {
    private static final long serialVersionUID = 5808415173145957468L;

    private RedisUtils redisUtils;


    // 执行监听事件  eventName：start take end 现在只用到end
    @Override
    public void notify(DelegateExecution delegateExecution) {
        if(delegateExecution instanceof ExecutionEntity){
            if("ACTIVITY_DELETED".equals(((ExecutionEntity)delegateExecution).getDeleteReason()))return;
        }
        String auditUserIdVal = ServletUtils.getRequest().getHeader(Constants.CURRENT_ID);
        // complete:在任务完成后，且被从运行时数据（runtime data）中删除前触发。
        //获取BEAN
        IBizBusinessService bizBusinessService = ApplicationContextUtil.getBean(IBizBusinessService.class);
        IActSmsSupplementaryOrderService actSmsSupplementaryOrderService = ApplicationContextUtil.getBean(IActSmsSupplementaryOrderService.class);
//        RuntimeService runtimeService = delegateExecution.getEngineServices().getRuntimeService();
        Set<String> variableNames = delegateExecution.getVariableNames();
        for (String key : variableNames) {
            Object value = delegateExecution.getVariable(key);
            log.info("参数{}={}", key, value);
        }
        //处理业务逻辑
        if (ObjectUtil.isEmpty(delegateExecution.getVariable("result"))) {
            return;
        }
        String resultVar = delegateExecution.getVariable("result").toString();
        String taskIdVar = delegateExecution.getVariable("taskIdVar").toString();
        String bizBusinessId = delegateExecution.getVariable("bizBusinessId").toString();
        String comment = new String();
        if (delegateExecution.getVariable("comment") != null) {
            comment = delegateExecution.getVariable("comment").toString();
        }
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizBusinessId);

        Integer result = Integer.parseInt(resultVar);
        BizAudit bizAudit = new BizAudit();
        bizAudit.setTaskId(taskIdVar);
        bizAudit.setResult(result);
        bizAudit.setProcDefKey(bizBusiness.getProcDefKey());
        bizAudit.setProcName(bizBusiness.getProcName());
        bizAudit.setApplyer(bizBusiness.getApplyer());
        bizAudit.setProcInstId(bizBusiness.getProcInstId());
        bizAudit.setBusinessKey(bizBusiness.getId());
        bizAudit.setComment(comment);

        R r = actSmsSupplementaryOrderService.auditLogic(bizAudit, Long.valueOf(auditUserIdVal));
        if (r.isSuccess()) {
            bizAudit = BeanUtil.toBean(r.getObj("bizAudit"), BizAudit.class);
            Set<String> userIds = (Set<String>) r.get("userIds");
            String userId = r.getStr("userId");
            redisUtils = ApplicationContextUtil.getBean(RedisUtils.class);
            redisUtils.set(StrUtil.format("bizAudit{}{}", bizBusiness.getId(), taskIdVar), bizAudit, 50);
            redisUtils.set(StrUtil.format("userIds{}{}", bizBusiness.getId(), taskIdVar), userIds, 50);
            redisUtils.set(StrUtil.format("userId{}{}", bizBusiness.getId(), taskIdVar), userId, 50);
        }
    }
}
