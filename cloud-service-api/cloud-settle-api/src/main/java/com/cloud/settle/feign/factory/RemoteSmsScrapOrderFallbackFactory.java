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
                return null;
            }
            /**
             * 修改保存报废管理申请  -- 无状态校验
             * @param smsScrapOrder
             * @return 是否成功
             */
            @Override
            public R update(SmsScrapOrder smsScrapOrder) {
                return R.error();
            }
            /**
             * 修改保存报废管理申请  --有状态校验
             * @param smsScrapOrder
             * @return 是否成功
             */
            @Override
            public R editSave(SmsScrapOrder smsScrapOrder) {
                return R.error("服务被降级熔断。。");
            }

            /**
             * 新增保存报废申请
             * @param smsScrapOrder
             * @return 是否成功
             */
            @Override
            public R addSave(SmsScrapOrder smsScrapOrder) {
                return  R.error();
            }

            /**
             * 定时任务更新指定月份销售价格到报废表
             * @param month
             * @return
             */
            @Override
            public R updatePriceEveryMonth(String month) {
                return R.error();
            }
            /**
             * 定时任务更新指定月份SAP销售价格
             * @param month
             * @return
             */
            @Override
            public R updateSAPPriceEveryMonth(String month) {
                return R.error();
            }

            /**
             * 业务科审批通过传SAP261
             * @param smsScrapOrder
             * @return
             */
            @Override
            public R autidSuccessToSAP261(SmsScrapOrder smsScrapOrder) {
                return R.error();
            }
        };
    }
}
