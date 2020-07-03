package com.cloud.system.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.SysUserScope;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * 角色和部门关联Mapper接口
 *
 * @author cs
 * @date 2020-05-02
 */
public interface SysUserScopeMapper extends BaseMapper<SysUserScope>{

    String selectDataScopeIdByUserIdAndType(@Param("userId")Long userId, @Param("type")String type);

    /**
     * 物理删除
     * @param userId
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户id查询权限
     * @param userId
     * @return
     */
    Set<String> selectDataScopeIdByUserId(@Param("userId")Long userId);


}
