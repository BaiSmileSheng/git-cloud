package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RemoteCdMaterialPriceInfoFallbackFactory implements FallbackFactory<RemoteCdMaterialPriceInfoService> {


    @Override
    public RemoteCdMaterialPriceInfoService create(Throwable throwable) {
        log.error("RemoteCdMaterialPriceInfoService错误信息：{}",throwable.getMessage());
        return new RemoteCdMaterialPriceInfoService(){
            /**
             * 根据Example条件查询列表
             * @param materialCode
             * @param beginDate
             * @param endDate
             * @return List<CdMaterialPriceInfo>
             */
            @Override
            public List<CdMaterialPriceInfo> findByMaterialCode(String materialCode, String purchasingGroup,String beginDate, String endDate) {
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

            /**
             * 根据物料号查询
             * @param materialCodes
             * @param beginDate
             * @param endDate
             * @return Map<materialCode,CdMaterialPriceInfo>
             */
            @Override
            public Map<String, CdMaterialPriceInfo> selectPriceByInMaterialCodeAndDate(String materialCodes, String beginDate, String endDate) {
                return null;
            }

            /**
             * 定时加工费/原材料价格同步
             * @return 成功或失败
             */
            @Override
            public R synPriceJGF() {
                log.error("RemoteCdMaterialPriceInfoService.synPriceJGF ：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 定时加工费/原材料价格同步
             * @return 成功或失败
             */
            @Override
            public R synPriceYCL() {
                log.error("RemoteCdMaterialPriceInfoService.synPriceYCL ：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            @Override
            public R selectOneByCondition(String materialCode, String purchasingOrganization, String memberCode) {
                log.error("RemoteCdMaterialPriceInfoService.selectOneByCondition ：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
            /**
             * 根据成品物料号查询SAP成本价格
             * @param list
             * @return R
             */
            @Override
            public R selectMaterialPrice(List<CdSettleProductMaterial> list) {
                log.error("RemoteCdMaterialPriceInfoService.selectMaterialPrice ：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

        };
    }
}
