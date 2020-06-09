package com.cloud.system.feign.factory;

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
        log.error(throwable.getMessage());
        return new RemoteCdSettleProductMaterialService() {

            /**
             * 根据成品物料编码 查物料号和加工费号对应关系
             * @param productMaterialCode 成品物料编码
             * @param rawMaterialCode 加工费号
             * @return 物料号和加工费号对应关系列表
             */
            @Override
            public List<CdSettleProductMaterial> listByCode(String productMaterialCode, String rawMaterialCode) {
                return null;
            }
        };
    }
}
