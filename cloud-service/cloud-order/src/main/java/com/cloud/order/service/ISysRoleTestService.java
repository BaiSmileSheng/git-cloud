package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysRole;

/**
 * 测试 接口调用日志
 * @Author Lihongxia
 * @Date 2020-05-22
 */
public interface ISysRoleTestService {

    /**
     * 测试新增保存角色 与 调用接口日志信息
     * @param sysRole 角色信息
     * @return R 成功或失败
     */
    R addSave(SysRole sysRole);
}
