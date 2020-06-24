package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.feign.factory.RemoteUserSocopeFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 数据权限Feign服务层
 *
 * @author cs
 * @date 2020-05-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserSocopeFallbackFactory.class)
public interface RemoteUserScopeService {
    /**
     * 根据用户Id和类型获取用户物料权限
     *
     * @param userId
     * @return
     * @author zmr
     */
    @GetMapping("userScope/getScopes")
    public String selectDataScopeIdByUserIdAndType(@RequestParam("userId") Long userId,@RequestParam("type") String type);

}
