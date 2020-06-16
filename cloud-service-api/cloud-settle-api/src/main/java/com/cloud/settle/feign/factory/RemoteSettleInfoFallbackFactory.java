package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.feign.RemoteSettleInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
        return new RemoteSettleInfoService(){

            /**
             * 分页查询加工费结算 列表
             * @param smsSettleInfo 订单结算信息--加工费结算信息
             * @return null
             */
            @Override
            public TableDataInfo list(SmsSettleInfo smsSettleInfo) {
                log.error("RemoteSettleInfoService.list错误：{}",throwable.getMessage());
                return null;
            }

            /**
             * 查询加工费结算 列表
             * @param smsSettleInfo 订单结算信息--加工费结算信息
             * @return R 查询失败
             */
            @Override
            public List<SmsSettleInfo> listByCondition(SmsSettleInfo smsSettleInfo) {
                log.error("RemoteSettleInfoService.listByCondition错误：{}",throwable.getMessage());
                return null;
            }

            /**
             * 修改保存加工费结算
             * @param smsSettleInfo 加工费结算信息
             * @return R 修改失败
             */
            @Override
            public R editSave(SmsSettleInfo smsSettleInfo) {
                log.error("RemoteSettleInfoService.editSave错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }

            /**
             * 新增保存加工费结算
             * @param smsSettleInfo 加工费结算信息
             * @return R 新增失败
             */
            @Override
            public R addSave(SmsSettleInfo smsSettleInfo) {
                log.error("RemoteSettleInfoService.addSave错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }

            /**
             * 计算加工费(定时任务调用)
             * @return 成功或失败
             */
            @Override
            public R smsSettleInfoCalculate() {
                log.error("RemoteSettleInfoService.smsSettleInfoCalculate错误：{}",throwable.getMessage());
                return R.error("服务器拥挤：请稍后再试！");
            }
        };
    }
}
