package com.cloud.order.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.order.service.ISysInterfaceLogTestService;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.feign.RemoteInterfaceLogService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Lihongxia
 * @Date 2020-05-22
 */
@Service(value = "sysInterfaceLogTestService")
@Slf4j
public class SysInterfaceLogTestServiceImpl implements ISysInterfaceLogTestService {

    @Autowired
    public RemoteInterfaceLogService remoteInterfaceLogService;

    @GlobalTransactional
    @Override
    public R addSave(SysInterfaceLog sysInterfaceLog) {
        R r = remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        return r;
    }

}
