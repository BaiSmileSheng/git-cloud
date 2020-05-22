package com.cloud.settle.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SettleTestAct;
import com.cloud.settle.feign.RemoteSettleTestActService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteSettleTestActFallbackFactory implements FallbackFactory<RemoteSettleTestActService> {


    @Override
    public RemoteSettleTestActService create(Throwable throwable) {
        return new RemoteSettleTestActService(){

            @Override
            public SettleTestAct get(Long id) {
                return null;
            }

            @Override
            public R addSave(SettleTestAct settleTestAct) {
                return null;
            }

            @Override
            public R editSave(SettleTestAct settleTestAct) {
                return null;
            }
        };
    }
}
