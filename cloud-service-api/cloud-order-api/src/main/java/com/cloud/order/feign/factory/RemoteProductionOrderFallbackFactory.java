package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteProductionOrderFallbackFactory implements FallbackFactory<RemoteProductionOrderService> {


    @Override
    public RemoteProductionOrderService create(Throwable throwable) {
        log.error("RemoteProductionOrderService(生产订单)错误信息：{}",throwable.getMessage());
        return new RemoteProductionOrderService(){

            @Override
            public R selectByProdctOrderCode(String prodctOrderCode) {
                return R.error("服务拥挤请稍后再试");
            }

            /**
             * 查询排产订单 列表
             * @param productEndDateEnd  基本结束时间 结束值
             * @param actualEndDateStart 实际结束时间 起始值
             * @param actualEndDateEnd 实际结束时间 结束值
             * @return 排产订单 列表
             */
            @Override
            public List<OmsProductionOrder> listForDelays(String productEndDateEnd, String actualEndDateStart, String actualEndDateEnd) {
                return null;
            }


        };
    }
}
