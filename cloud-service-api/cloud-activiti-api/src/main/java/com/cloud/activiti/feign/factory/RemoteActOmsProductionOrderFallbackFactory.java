package com.cloud.activiti.feign.factory;

import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActStartProcessVo;
import com.cloud.activiti.feign.RemoteActOmsProductionOrderService;
import com.cloud.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteActOmsProductionOrderFallbackFactory implements FallbackFactory<RemoteActOmsProductionOrderService> {

    @Override
    public RemoteActOmsProductionOrderService create(Throwable throwable) {
        return new RemoteActOmsProductionOrderService() {
            /**
             * Description:  开启审批流
             * Param: [key, orderId, orderCode, userId, title]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/24
             */
            @Override
            public R startActProcess(ActBusinessVo actBusinessVo) {
                log.error("开启排产订单审批流程失败，原因{}：" + throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
