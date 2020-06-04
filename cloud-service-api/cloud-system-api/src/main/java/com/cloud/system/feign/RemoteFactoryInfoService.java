package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.feign.factory.RemoteFactoryInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 工厂信息 Feign服务层
 * @author cs
 * @date 2020-06-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteFactoryInfoFallbackFactory.class)
public interface RemoteFactoryInfoService {
    /**
     * 查询工厂信息
     * @param factoryCode
     * @return 工厂信息
     */
    @GetMapping("factoryInfo/getOne")
    CdFactoryInfo selectOneByFactory(@RequestParam(value = "factoryCode") String factoryCode);
}
