package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteCdMaterialPriceInfoFallbackFactory implements FallbackFactory<RemoteCdMaterialPriceInfoService> {


    @Override
    public RemoteCdMaterialPriceInfoService create(Throwable throwable) {
        return new RemoteCdMaterialPriceInfoService(){
            /**
             * 根据Example条件查询列表
             * @param materialCode
             * @param beginDate
             * @param endDate
             * @return List<CdMaterialPriceInfo>
             */
            @Override
            public List<CdMaterialPriceInfo> findByMaterialCode(String materialCode, String beginDate, String endDate) {
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

            @Override
            public R checkIsMinUnit(String materialCode, int applyNum) {
                return R.error("校验申请数量是否是最小包装量的整数倍失败");
            }
        };
    }
}
