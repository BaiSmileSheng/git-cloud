package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteMaterialService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteMaterialFactory implements FallbackFactory<RemoteMaterialService> {
    @Override
    public RemoteMaterialService create(Throwable throwable) {

        return new RemoteMaterialService() {
            @Override
            public R saveMaterialInfo() {
                log.error("RemoteMaterialService.saveMaterialInfo错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            @Override
            public R updateUphBySap() {
                log.error("RemoteMaterialService.updateUphBySap错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 根据物料号查询物料信息
             * @param materialCode
             * @return
             */
            @Override
            public R getByMaterialCode(String materialCode) {
                log.error("RemoteMaterialService.getByMaterialCode错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤请稍后再试");
            }

            /**
             * 根据物料号集合查询物料信息
             * @param materialCodes
             * @return
             */
            @Override
            public R selectInfoByInMaterialCodeAndMaterialType(List<String> materialCodes,String materialType) {
                log.error("RemoteMaterialService.selectInfoByInMaterialCodeAndMaterialType错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
        };
    }
}
