package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.feign.RemoteCdSapSalePriceInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RemoteCdSapSalePriceInfoFallbackFactory implements FallbackFactory<RemoteCdSapSalePriceInfoService> {


    @Override
    public RemoteCdSapSalePriceInfoService create(Throwable throwable) {
        return new RemoteCdSapSalePriceInfoService(){

            /**
             * 根据Example条件查询列表
             * @param materialCode
             * @param beginDate
             * @param endDate
             * @return null
             */
            @Override
            public List<CdSapSalePrice> findByMaterialCodeAndOraganization(String materialCode, String oraganization,String beginDate, String endDate) {
                return null;
            }

            /**
             * 根据专用号和销售组织分组查询
             * @param materialCodes
             * @param beginDate
             * @param endDate
             * @return null
             */
            @Override
            public Map<String, CdSapSalePrice> selectPriceByInMaterialCodeAndDate(String materialCodes, String beginDate, String endDate) {
                return null;
            }
        };
    }
}
