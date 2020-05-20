package com.cloud.system.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.mapper.SysInterfaceLogMapper;
import com.cloud.system.service.ISysInterfaceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 接口调用日志service业务层处理
 *
 * @author cs
 * @date 2020-05-20
 */
@Service
public class SysInterfaceLogServiceImpl extends BaseServiceImpl<SysInterfaceLog> implements ISysInterfaceLogService {
    @Autowired
    private SysInterfaceLogMapper sysInterfaceLogMapper;


}
