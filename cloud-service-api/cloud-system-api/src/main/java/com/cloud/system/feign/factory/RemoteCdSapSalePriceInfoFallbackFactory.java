package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.feign.RemoteCdSapSalePriceInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteCdSapSalePriceInfoFallbackFactory implements FallbackFactory<RemoteCdSapSalePriceInfoService> {


    @Override
    public RemoteCdSapSalePriceInfoService create(Throwable throwable) {
        return new RemoteCdSapSalePriceInfoService(){


            @Override
            public List<CdSapSalePrice> findByMaterialCode(String materialCode, String beginDate, String endDate) {
                return null;
            }
        };
    }
}
