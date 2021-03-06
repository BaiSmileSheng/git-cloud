package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.feign.RemoteSmsScrapOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSmsScrapOrderFallbackFactory implements FallbackFactory<RemoteSmsScrapOrderService> {


    @Override
    public RemoteSmsScrapOrderService create(Throwable throwable) {

        return new RemoteSmsScrapOrderService(){

            /**
             * 根据ID查询报废管理申请表
             * @param id
             * @return SmsScrapOrder
             */
            @Override
            public SmsScrapOrder get(Long id) {
                log.error("RemoteSmsScrapOrderService.get错误：{}",throwable.getMessage());
                return null;
            }
            /**
             * 修改保存报废管理申请  -- 无状态校验
             * @param smsScrapOrder
             * @return 是否成功
             */
            @Override
            public R update(SmsScrapOrder smsScrapOrder) {
                log.error("RemoteSmsScrapOrderService.update错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 修改保存报废管理申请  --有状态校验
             * @param smsScrapOrder
             * @return 是否成功
             */
            @Override
            public R editSave(SmsScrapOrder smsScrapOrder) {
                log.error("RemoteSmsScrapOrderService.editSave报废申请编辑服务熔断降级，原因是：{}", throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }

            /**
             * 新增保存报废申请
             * @param smsScrapOrder
             * @return 是否成功
             */
            @Override
            public R addSave(SmsScrapOrder smsScrapOrder) {
                log.error("RemoteSmsScrapOrderService.addSave错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }

            /**
             * 定时任务更新指定月份销售价格到报废表
             * @param month
             * @return
             */
            @Override
            public R updatePriceEveryMonth(String month) {
                log.error("RemoteSmsScrapOrderService.updatePriceEveryMonth错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
            /**
             * 定时任务更新指定月份SAP销售价格
             * @param month
             * @return
             */
            @Override
            public R updateSAPPriceEveryMonth(String month) {
                log.error("RemoteSmsScrapOrderService.updateSAPPriceEveryMonth错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }

            /**
             * 业务科审批通过传SAP261
             * @param smsScrapOrder
             * @return
             */
            @Override
            public R autidSuccessToSAP261(SmsScrapOrder smsScrapOrder) {
                log.error("RemoteSmsScrapOrderService.autidSuccessToSAP261错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
        };
    }
}
