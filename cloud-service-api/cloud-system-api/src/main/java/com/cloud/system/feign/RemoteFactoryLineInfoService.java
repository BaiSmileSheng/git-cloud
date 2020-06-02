package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.feign.factory.RemoteFactoryLineInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 工厂线体关系 Feign服务层
 *
 * @author cs
 * @date 2020-06-01
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteFactoryLineInfoFallbackFactory.class)
public interface RemoteFactoryLineInfoService {
    /**
     * 查询工厂线体关系
     * @param cdFactoryLineInfo
     * @return List<CdFactoryLineInfo>
     */
    @PostMapping("factoryLine/listByExample")
    R listByExample(@RequestBody CdFactoryLineInfo cdFactoryLineInfo);
}
