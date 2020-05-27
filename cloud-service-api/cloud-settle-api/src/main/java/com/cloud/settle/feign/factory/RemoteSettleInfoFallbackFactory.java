package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.feign.RemoteSettleInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 加工费结算
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Slf4j
@Component
public class RemoteSettleInfoFallbackFactory implements FallbackFactory<RemoteSettleInfoService> {
    @Override
    public RemoteSettleInfoService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteSettleInfoService(){

            /**
             * 查询加工费结算 列表
             * @param smsSettleInfo 订单结算信息--加工费结算信息
             * @return TableDataInfo 加工费结算分页列表
             */
            @Override
            public TableDataInfo list(SmsSettleInfo smsSettleInfo) {
                return null;
            }
            /**
             * 修改保存加工费结算
             * @param smsSettleInfo 加工费结算信息
             * @return R 修改成功或失败
             */
            @Override
            public R editSave(SmsSettleInfo smsSettleInfo) {
                return null;
            }

            /**
             * 新增保存加工费结算
             * @param smsSettleInfo 加工费结算信息
             * @return R 修改成功或失败
             */
            @Override
            public R addSave(SmsSettleInfo smsSettleInfo) {
                return null;
            }
        };
    }
}
