package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Component
public class RemoteCdMaterialPriceInfoFallbackFactory implements FallbackFactory<RemoteCdMaterialPriceInfoService> {


    @Override
    public RemoteCdMaterialPriceInfoService create(Throwable throwable) {
        return new RemoteCdMaterialPriceInfoService(){
            /**
             * 根据Example条件查询列表
             * @param example
             * @return List<CdMaterialPriceInfo>
             */
            @Override
            public R findByExample(Example example) {
                return null;
            }

            /**
             * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
             * @param materialCode
             * @return R CdMaterialPriceInfo对象
             */
            @Override
            public R checkSynchroSAP(String materialCode) {
                return R.error("校验物料号同步sap价格失败");
            }
        };
    }
}
