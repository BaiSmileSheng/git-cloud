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
        log.error(throwable.getMessage());
        return new RemoteSettleInfoService(){

            /**
             * 分页查询加工费结算 列表
             * @param smsSettleInfo 订单结算信息--加工费结算信息
             * @return null
             */
            @Override
            public TableDataInfo list(SmsSettleInfo smsSettleInfo) {
                return null;
            }

            /**
             * 查询加工费结算 列表
             * @param smsSettleInfo 订单结算信息--加工费结算信息
             * @return R 查询失败
             */
            @Override
            public List<SmsSettleInfo> listByCondition(SmsSettleInfo smsSettleInfo) {
                return null;
            }

            /**
             * 修改保存加工费结算
             * @param smsSettleInfo 加工费结算信息
             * @return R 修改失败
             */
            @Override
            public R editSave(SmsSettleInfo smsSettleInfo) {

                return R.error("调用settle系统修改加工费失败");
            }

            /**
             * 新增保存加工费结算
             * @param smsSettleInfo 加工费结算信息
             * @return R 新增失败
             */
            @Override
            public R addSave(SmsSettleInfo smsSettleInfo) {
                return R.error("调用settle系统新增加工费失败");
            }
        };
    }
}
