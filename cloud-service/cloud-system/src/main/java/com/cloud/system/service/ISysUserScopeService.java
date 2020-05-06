package com.cloud.system.service;

import com.cloud.system.domain.entity.SysUserScope;
import com.cloud.common.core.service.BaseService;

/**
 * 角色和部门关联Service接口
 *
 * @author cs
 * @date 2020-05-02
 */
public interface ISysUserScopeService extends BaseService<SysUserScope>{

    /**
     * 根据用户Id获取用户物料权限
     * @param userId
     * @return
     */
    String selectDataScopeIdByUserId(Long userId);

    /**
     * 物理删除用户当前物料权限
     * @param userId
     */
    void deleteByUserId(Long userId);
}
