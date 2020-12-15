package com.cloud.activiti.feign.factory;

import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.feign.RemoteActOmsProductionOrderService;
import com.cloud.activiti.feign.RemoteActSmsRawScrapOrderService;
import com.cloud.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteActSmsRawScrapOrderFallbackFactory implements FallbackFactory<RemoteActSmsRawScrapOrderService> {

    @Override
    public RemoteActSmsRawScrapOrderService create(Throwable throwable) {
        return new RemoteActSmsRawScrapOrderService() {
            /**
             * 原材料报废审核开启流程  提交(编辑、新增提交)
             *
             * @param actBusinessVo
             * @return R 成功/失败
             */
            @Override
            public R addSave(ActBusinessVo actBusinessVo) {
                log.error("开启原材料报废审批流程失败，原因{}：" + throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
