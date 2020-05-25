package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysInterfaceLog;

/**
 * 测试 接口调用日志
 * @Author Lihongxia
 * @Date 2020-05-22
 */
public interface ISysInterfaceLogTestService{

    /**
     * 新增 接口调用日志
     * @param sysInterfaceLog 接口调用日志信息
     * @return R {"code":0,"msg":"success","data":"新增"接口调用日志表的主键id}
     */
    R addSave(SysInterfaceLog sysInterfaceLog);
}
