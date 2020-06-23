package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.feign.RemoteDelaysDeliveryService;
import com.cloud.settle.feign.RemoteQualityOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 延期索赔 提供者
 * @Author Lihongxia
 * @Date 2020-06-01
 */
@Slf4j
@Component
public class RemoteDelaysDeliveryFallbackFactory implements FallbackFactory<RemoteDelaysDeliveryService> {
    @Override
    public RemoteDelaysDeliveryService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteDelaysDeliveryService(){

            /**
             * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
             * @return 成功或失败
             */
            @Override
            public R batchAddDelaysDelivery() {
                log.error("RemoteDelaysDeliveryService.batchAddDelaysDelivery error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 查询延期交付索赔
             * @param id
             * @return 延期交付索赔信息
             */
            @Override
            public R get(Long id) {
                log.error("RemoteDelaysDeliveryService.get error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 查询延期交付索赔详情
             * @param id 主键id
             * @return 延期交付索赔详情(包含文件信息)
             */
            @Override
            public R selectById(Long id) {
                log.error("RemoteDelaysDeliveryService.selectById error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 修改延期索赔信息
             * @param smsDelaysDelivery 延期索赔信息
             * @return 修改数量
             */
            @Override
            public R editSave(SmsDelaysDelivery smsDelaysDelivery) {
                log.error("RemoteDelaysDeliveryService.editSave error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 48H超时未确认发送邮件
             * @return 成功或失败
             */
            @Override
            public R overTimeSendMail() {
                log.error("RemoteDelaysDeliveryService.overTimeSendMail error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 72H超时供应商自动确认
             * @return 成功或失败
             */
            @Override
            public R overTimeConfim() {
                log.error("RemoteDelaysDeliveryService.overTimeConfim error:{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
