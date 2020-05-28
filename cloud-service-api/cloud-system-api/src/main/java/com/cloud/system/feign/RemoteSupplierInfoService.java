package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.domain.entity.CdSupplierInfo;
import com.cloud.system.feign.factory.RemoteSupplierInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用户 Feign服务层
 * @author cs
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSupplierInfoFallbackFactory.class)
public interface RemoteSupplierInfoService {
    /**
     * 根据登录名查询供应商信息
     * @param loginName
     * @return CdSupplierInfo
     */
    @GetMapping("supplier/getByNick")
    CdSupplierInfo getByNick(String loginName);
}
