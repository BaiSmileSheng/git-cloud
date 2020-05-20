package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.feign.factory.RemoteInterfaceLogFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 接口调用日志
 *
 * @author Lihongxia
 * @date 2020-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteInterfaceLogFallbackFactory.class)
public interface RemoteInterfaceLogService {

    /**
     * 新增保存接口调用日志表
     * @param sysInterfaceLog 接口调用日志
     * @return R 新增结果,成功code:0或失败code:500
     */
    @PostMapping("/interfaceLog/save")
    public R saveInterfaceLog(SysInterfaceLog sysInterfaceLog);

    /**
     * 修改保存接口调用日志表
     * @param sysInterfaceLog 接口调用日志
     * @return 修改结果,成功code:0或失败code:500
     */
    @PostMapping("/interfaceLog/update")
    public R updateInterfaceLog(SysInterfaceLog sysInterfaceLog);
}
