package com.cloud.order.service.impl;
import java.util.Date;

import com.cloud.common.core.domain.R;
import com.cloud.order.service.ISysRoleTestService;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.domain.entity.SysRole;
import com.cloud.system.feign.RemoteInterfaceLogService;
import com.cloud.system.feign.RemoteRoleService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 角色实现类
 * @Author Lihongxia
 * @Date 2020-05-25
 */
@Service(value = "sysRoleTestService")
public class SysRoleTestServiceImpl implements ISysRoleTestService {

    @Autowired
    private RemoteRoleService remoteRoleService;

    @Autowired
    private RemoteInterfaceLogService  remoteInterfaceLogService;

    @GlobalTransactional
    @Override
    public R addSave(SysRole sysRole) {
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog();
        sysInterfaceLog.setAppId("4561");
        sysInterfaceLog.setOrderCode("456");
        sysInterfaceLog.setInterfaceName("456");
        sysInterfaceLog.setContent("456");
        sysInterfaceLog.setResults("456");
        sysInterfaceLog.setDelFlag("1");
        sysInterfaceLog.setSearchValue("456");
        sysInterfaceLog.setCreateBy("456");
        sysInterfaceLog.setCreateTime(new Date());
        sysInterfaceLog.setUpdateBy("456");
        sysInterfaceLog.setUpdateTime(new Date());
        sysInterfaceLog.setRemark("456");
        sysInterfaceLog.setDelFlag("1");
        R rInterfaceLog = remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        System.out.println("======================saveInterfaceLog 主键id"+rInterfaceLog.get("data"));
        int i = 1/0;
        return remoteRoleService.addSave(sysRole);
    }
}
