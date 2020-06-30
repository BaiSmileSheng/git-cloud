package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.order.feign.RemoteDemandOrderGatherEditService;
import com.cloud.order.feign.RemoteDemandOrderGatherService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteDemandOrderGatherEditFallbackFactory implements FallbackFactory<RemoteDemandOrderGatherEditService> {


    @Override
    public RemoteDemandOrderGatherEditService create(Throwable throwable) {

        return new RemoteDemandOrderGatherEditService(){

            /**
             * 查询 滚动计划需求操作
             * @param id
             * @return
             */
            @Override
            public R get(Long id) {
                log.error("RemoteDemandOrderGatherEditService.get：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 修改 滚动计划需求操作
             * @param omsDemandOrderGatherEdit
             * @return
             */
            @Override
            public R updateGatherEdit(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
                log.error("RemoteDemandOrderGatherEditService.updateGatherEdit：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

        };
    }
}
