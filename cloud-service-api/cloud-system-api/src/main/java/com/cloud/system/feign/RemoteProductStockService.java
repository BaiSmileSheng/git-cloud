package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteSapSystemInterfaceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 成品库存主表 Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-13
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSapSystemInterfaceFallbackFactory.class)
public interface RemoteProductStockService {
    /**
     * 定时任务同步成品库存
     *
     * @return
     */
    @PostMapping("productStock/timeSycProductStock")
    R timeSycProductStock();
}
