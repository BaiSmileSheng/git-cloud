package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysInterfaceLog;

/**
 * 接口调用日志 调用测试
 * @Author Lihongxia
 * @Date 2020-05-22
 */
public interface ISysInterfaceLogTestService{

    R addSave(SysInterfaceLog sysInterfaceLog);
}
