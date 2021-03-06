package com.cloud.system.feign.factory;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.RemoteBomService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class RemoteBomFallbackFactory implements FallbackFactory<RemoteBomService> {

    @Override
    public RemoteBomService create(Throwable throwable) {
        log.error("RemoteBomService错误信息：{}",throwable.getMessage());
        return new RemoteBomService() {
            /**
             * 根据成品物料号、原材料物料号确定一条数据
             * @param productMaterialCode
             * @param rawMaterialCode
             * @return
             */
            @Override
            public R listByProductAndMaterial(String productMaterialCode, String rawMaterialCode,String bomVersion,String productFactoryCode) {
                log.error("RemoteBomService.listByProductAndMaterial错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
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
                log.error("RemoteBomService.checkBomNum错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
            /**
             * 根据物料号工厂分组取bom版本
             * @param dicts
             * @return
             */
            @Override
            public R selectVersionMap(List<Dict> dicts) {
                log.error("RemoteBomService.checkBomNum错误信息：{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
            /**
             * Description:  根据成品专用号、生产工厂、版本查询
             * Param: [list]
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/18
             */
            @Override
            public R selectBomList(List<Dict> list) {
                return R.error("服务器拥挤，请稍后再试！");
            }


        };
    }
}
