package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.feign.RemoteSmsSupplementaryOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSmsSupplementaryOrderFallbackFactory implements FallbackFactory<RemoteSmsSupplementaryOrderService> {


    @Override
    public RemoteSmsSupplementaryOrderService create(Throwable throwable) {
        return new RemoteSmsSupplementaryOrderService(){

            /**
             * 根据ID查询物耗管理申请表
             * @param id
             * @return null
             */
            @Override
            public SmsSupplementaryOrder get(Long id) {
                return null;
            }

            /**
             * 修改保存物耗管理申请  -- 无状态校验
             * @param smsSupplementaryOrder
             * @return 错误信息
             */
            @Override
            public R update(SmsSupplementaryOrder smsSupplementaryOrder) {
                return R.error();
            }

            /**
             * 修改保存物耗管理申请  --有状态校验
             * @param smsSupplementaryOrder
             * @return 错误信息
             */
            @Override
            public R editSave(SmsSupplementaryOrder smsSupplementaryOrder) {
                return R.error();
            }

            /**
             * 新增保存物耗申请单
             * @param smsSupplementaryOrder
             * @return 是否成功
             */
            @Override
            public R addSave(SmsSupplementaryOrder smsSupplementaryOrder) {
                return R.error();
            }

            /**
             * 定时任务更新指定月份原材料价格到物耗表
             * @param month
             * @return
             */
            @Override
            public R updatePriceEveryMonth(String month) {
                return R.error();
            }
            /**
             * 小微主审批通过传SAPY61
             * @param smsSupplementaryOrder
             * @return
             */
            @Override
            public R autidSuccessToSAPY61(SmsSupplementaryOrder smsSupplementaryOrder) {
                return R.error();
            }
        };
    }
}
