package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.feign.RemoteQualityOrderService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 质量索赔
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@Slf4j
@Component
public class RemoteQualityOrderFallbackFactory implements FallbackFactory<RemoteQualityOrderService> {
    @Override
    public RemoteQualityOrderService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteQualityOrderService(){
            /**
             * 查询质量索赔
             * @param id 主键id
             * @return SmsQualityOrder 质量索赔信息
             */
            @Override
            public SmsQualityOrder get(Long id) {
                return null;
            }

            /**
             * 查询质量索赔详情
             * @param id 主键id
             * @return 质量索赔信息详情(包含文件信息)
             */
            @Override
            public R selectById(Long id) {
                return null;
            }

            /**
             * 修改保存质量索赔
             * @param smsQualityOrder 质量索赔信息
             * @return 修改成功或失败
             */
            @Override
            public R editSave(SmsQualityOrder smsQualityOrder) {
                return null;
            }
        };
    }
}
