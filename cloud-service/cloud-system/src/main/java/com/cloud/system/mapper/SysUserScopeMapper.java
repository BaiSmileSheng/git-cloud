package com.cloud.system.mapper;

import com.cloud.system.domain.entity.SysUserScope;
import com.cloud.common.core.dao.BaseMapper;

import java.util.List;

/**
 * 角色和部门关联Mapper接口
 *
 * @author cs
 * @date 2020-05-02
 */
public interface SysUserScopeMapper extends BaseMapper<SysUserScope>{

    String selectDataScopeIdByUserId(Long userId);

    /**
     * 物理删除
     * @param userId
     */
    void deleteByUserId(Long userId);
}
