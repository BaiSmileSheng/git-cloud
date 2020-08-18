package com.cloud.order.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.order.feign.RemoteDemandOrderGatherEditHisService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteDemandOrderGatherEditHisFallbackFactory implements FallbackFactory<RemoteDemandOrderGatherEditHisService> {


    @Override
    public RemoteDemandOrderGatherEditHisService create(Throwable throwable) {

        return new RemoteDemandOrderGatherEditHisService(){

            /**
             * 查询 滚动计划需求操作历史
             * @param id
             * @return
             */
            @Override
            public R get(Long id) {
                log.error("RemoteDemandOrderGatherEditHisService.get：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
