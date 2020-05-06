package com.cloud.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.SysUserScopeMapper;
import com.cloud.system.domain.entity.SysUserScope;
import com.cloud.system.service.ISysUserScopeService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
/**
 * 用户和物料数据权限关联Service业务层处理
 *
 * @author cs
 * @date 2020-05-02
 */
@Service
public class SysUserScopeServiceImpl extends BaseServiceImpl<SysUserScope> implements ISysUserScopeService {
    @Autowired
    private SysUserScopeMapper sysUserScopeMapper;


    @Override
    public String selectDataScopeIdByUserId(Long userId) {
        return sysUserScopeMapper.selectDataScopeIdByUserId(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        sysUserScopeMapper.deleteByUserId(userId);
    }
}
