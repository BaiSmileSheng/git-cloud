package com.cloud.system.service;

import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.SysInterfaceLog;

/**
 * 接口调用日志 Service接口
 *
 * @author cs
 * @date 2020-05-20
 */
public interface ISysInterfaceLogService extends BaseService<SysInterfaceLog> {
    int insertSelectiveNoTransactional(SysInterfaceLog sysInterfaceLog);
}
