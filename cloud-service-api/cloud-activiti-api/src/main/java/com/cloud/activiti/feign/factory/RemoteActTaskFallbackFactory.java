package com.cloud.activiti.feign.factory;

import com.cloud.activiti.feign.RemoteActTaskService;
import com.cloud.activiti.feign.RemoteBizBusinessService;
import com.cloud.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RemoteActTaskFallbackFactory implements FallbackFactory<RemoteActTaskService> {

    @Override
    public RemoteActTaskService create(Throwable throwable) {
        return new RemoteActTaskService() {
            /**
             * Description:  根据业务订单号删除审批流程
             * Param: [orderCodeList]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/8/12
             */
            @Override
            public R deleteByOrderCode(Map<String, Object> map) {
                log.error("RemoteActTaskService，deleteByOrderCode{}：" + throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
