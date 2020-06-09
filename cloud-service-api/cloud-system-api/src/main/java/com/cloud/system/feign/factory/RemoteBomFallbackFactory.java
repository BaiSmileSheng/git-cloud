package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.feign.RemoteBomService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteBomFallbackFactory implements FallbackFactory<RemoteBomService> {

    @Override
    public RemoteBomService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteBomService() {
            /**
             * 根据成品物料号、原材料物料号确定一条数据
             * @param productMaterialCode
             * @param rawMaterialCode
             * @return
             */
            @Override
            public CdBomInfo listByProductAndMaterial(String productMaterialCode, String rawMaterialCode) {
                return null;
            }

            /**
             * 校验申请数量是否是单耗的整数倍
             * @param productMaterialCode
             * @param rawMaterialCode
             * @param applyNum
             * @return R
             */
            @Override
            public R checkBomNum(String rawMaterialCode, String productMaterialCode, int applyNum) {
                return null;
            }
        };
    }
}
