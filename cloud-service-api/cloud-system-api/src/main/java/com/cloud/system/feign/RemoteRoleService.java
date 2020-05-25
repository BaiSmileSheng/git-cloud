package com.cloud.system.feign;

import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteRoleFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.domain.entity.SysRole;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 角色 Feign服务层
 *
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteRoleFallbackFactory.class)
public interface RemoteRoleService {
    @GetMapping("role/get/{roleId}")
    public SysRole selectSysRoleByRoleId(@PathVariable("roleId") long roleId);

    /**
     * 新增角色信息
     * @param sysRole 角色信息
     * @return R 成功或失败
     */
    @PostMapping("role/save")
    public R addSave(@RequestBody SysRole sysRole);
}
