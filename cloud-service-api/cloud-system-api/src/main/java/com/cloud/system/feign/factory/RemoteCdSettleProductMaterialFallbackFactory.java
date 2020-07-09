package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.feign.RemoteCdSettleProductMaterialService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class RemoteCdSettleProductMaterialFallbackFactory implements FallbackFactory<RemoteCdSettleProductMaterialService> {

    @Override
    public RemoteCdSettleProductMaterialService create(Throwable throwable) {
        log.error("RemoteCdSettleProductMaterialService错误信息：{}",throwable.getMessage());
        return new RemoteCdSettleProductMaterialService() {

            /**
             * 根据成品物料编码 查物料号和加工费号对应关系
             * @param productMaterialCode 成品物料编码
             * @param rawMaterialCode 加工费号
             * @return 物料号和加工费号对应关系列表
             */
            @Override
            public R listByCode(String productMaterialCode, String rawMaterialCode) {
                log.error("RemoteCdSettleProductMaterialService.listByCode错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤,请稍后再试!");
            }

            @Override
            public R selectOne(String productMaterialCode, String outsourceWay) {
                log.error("RemoteCdSettleProductMaterialService.selectOne错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤,请稍后再试!");
            }
        };
    }
}
