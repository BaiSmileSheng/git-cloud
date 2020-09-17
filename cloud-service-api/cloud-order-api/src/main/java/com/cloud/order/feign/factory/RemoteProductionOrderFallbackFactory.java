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

        return new RemoteProductionOrderService(){

            /**
             * 根据生产订单号查询排产订单信息
             * @param prodctOrderCode
             * @return OmsProductionOrder
             */
            @Override
            public R selectByProdctOrderCode(String prodctOrderCode) {
                log.error("RemoteProductionOrderService.selectByProdctOrderCode(生产订单)错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

            /**
             * 查询排产订单 列表
             * @return 排产订单 列表
             */
            @Override
            public R listForDelays() {
                log.error("RemoteProductionOrderService.listForDelays(生产订单)错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }
            /**
             * Description:  根据id查询排产订单
             * Param: [id]
             * return: com.cloud.order.domain.entity.OmsProductionOrder
             * Author: ltq
             * Date: 2020/6/23
             */
            @Override
            public OmsProductionOrder get(Long id) {
                log.error("RemoteProductionOrderService.listForDelays(生产订单)错误信息：{}",throwable.getMessage());
                return null;
            }
            /**
             * 修改保存排产订单
             */
            @Override
            public R editSave(OmsProductionOrder omsProductionOrder) {
                log.error("RemoteProductionOrderService.editSave(排产订单更新)错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

            @Override
            public R timeSAPGetProductOrderCode() {
                log.error("RemoteProductionOrderService.timeSAPGetProductOrderCode错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

            @Override
            public R timeInsertSettleList() {
                log.error("RemoteProductionOrderService.timeInsertSettleList：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

            @Override
            public R timeGetConfirmAmont() {
                log.error("RemoteProductionOrderService.timeGetConfirmAmont：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

            @Override
            public R updateBatchByPrimary(List<OmsProductionOrder> omsProductionOrderList) {
                log.error("RemoteProductionOrderService.updateBatchByPrimary：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

        };
    }
}
