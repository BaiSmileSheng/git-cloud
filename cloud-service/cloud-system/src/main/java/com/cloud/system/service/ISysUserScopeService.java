package com.cloud.system.service;

import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.SysUserScope;

import java.util.Set;

/**
 * 用户和数据权限关联Service接口
 *
 * @author cs
 * @date 2020-05-02
 */
public interface ISysUserScopeService extends BaseService<SysUserScope>{

    /**
     * 根据用户Id和类型获取用户物料权限
     * @param userId
     * @return
     */
    String selectDataScopeIdByUserIdAndType(Long userId,String type);

    /**
     * 物理删除用户当前物料权限
     * @param userId
     */
    void deleteByUserId(Long userId);


    /**
     * 根据用户id查询权限
     * @param userId
     * @return
     */
    Set<String> selectDataScopeIdByUserId(Long userId);

}
