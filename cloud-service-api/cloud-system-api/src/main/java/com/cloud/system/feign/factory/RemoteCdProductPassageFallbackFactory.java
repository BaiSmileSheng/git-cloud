package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.feign.RemoteCdProductPassageService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:  成品在途明细
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/16
 */
@Component
@Slf4j
public class RemoteCdProductPassageFallbackFactory implements FallbackFactory<RemoteCdProductPassageService> {
    @Override
    public RemoteCdProductPassageService create(Throwable throwable) {
        return new RemoteCdProductPassageService() {
            /**
             * Description:  查询一条成品在途
             * Param: [cdProductPassage]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/16
             */
            @Override
            public R queryOneByExample(CdProductPassage cdProductPassage) {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
            /**
             * Description:  查询成品在途LIst
             * Param: [list]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/17
             */
            @Override
            public R queryByList(List<CdProductPassage> list) {
                log.error("服务拥挤，请稍后再试！原因："+throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }

        };
    }
}
