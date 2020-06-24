package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteSapSystemInterfaceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * system模块对SAP系统接口Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-13
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSapSystemInterfaceFallbackFactory.class)
public interface RemoteSapSystemInterfaceService {

    /**
     * 定时同步原材料库存
     *
     * @return
     */
    @PostMapping("sapSystem/sycRawMaterialStock")
    R sycRawMaterialStock();

    /**
     * 定时获取BOM清单数据
     * @return
     */
    @PostMapping("sapSystem/sycBomInfo")
    R sycBomInfo();

}
