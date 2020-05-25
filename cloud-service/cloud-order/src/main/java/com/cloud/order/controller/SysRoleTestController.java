package com.cloud.order.controller;

import com.cloud.common.core.domain.R;
import com.cloud.order.service.ISysRoleTestService;
import com.cloud.system.domain.entity.SysRole;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 角色测试
 * @Author Lihongxia
 * @Date 2020-05-26
 */
@RestController
@RequestMapping("sysRoleTestController")
@Slf4j
public class SysRoleTestController {

    @Autowired
    public ISysRoleTestService sysRoleTestService;

    /**
     * 测试新增保存角色 与 调用接口日志信息
     * @param sysRole 角色信息
     * @return R 成功或失败
     */
    @PostMapping("saveSysRoleTest")
    @ApiOperation(value = "测试新增保存角色", response = SysRole.class)
    public R savesysRoleTest(@RequestBody SysRole sysRole){
        return sysRoleTestService.addSave(sysRole);
    }
}
