package com.cloud.system.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.SysUserScope;
import com.cloud.system.mapper.SysUserScopeMapper;
import com.cloud.system.service.ISysUserScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
/**
 * 用户和数据权限关联Service业务层处理
 *
 * @author cs
 * @date 2020-05-02
 */
@Service
public class SysUserScopeServiceImpl extends BaseServiceImpl<SysUserScope> implements ISysUserScopeService {
    @Autowired
    private SysUserScopeMapper sysUserScopeMapper;


    /**
     * 根据用户Id和类型获取用户物料权限
     * @return
     */
    @Override
    public String selectDataScopeIdByUserIdAndType(Long userId,String type) {
        return sysUserScopeMapper.selectDataScopeIdByUserIdAndType(userId,type);
    }

    /**
     * 删除原有的权限
     * @param userId
     */
    @Override
    public void deleteByUserId(Long userId) {
        sysUserScopeMapper.deleteByUserId(userId);
    }

    /**
     * 根据用户id查询权限
     * @param userId
     * @return
     */
	@Override
	public Set<String> selectDataScopeIdByUserId(Long userId){
		 return sysUserScopeMapper.selectDataScopeIdByUserId(userId);
	}



}
