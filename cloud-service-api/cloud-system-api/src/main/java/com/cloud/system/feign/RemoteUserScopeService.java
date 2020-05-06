package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.feign.factory.RemoteUserSocopeFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户 Feign服务层
 *
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserSocopeFallbackFactory.class)
public interface RemoteUserScopeService {
    /**
     * 查询拥有当前角色的所有权限
     *
     * @param userId
     * @return
     * @author zmr
     */
    @GetMapping("userScope/getScopes")
    public String selectDataScopeIdByUserId(@RequestParam("userId") Long userId);

}
