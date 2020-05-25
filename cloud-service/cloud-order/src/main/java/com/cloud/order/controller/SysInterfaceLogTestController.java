package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.order.service.ISysInterfaceLogTestService;
import com.cloud.system.domain.entity.SysInterfaceLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口调用日志 调用测试
 * @Author Lihongxia
 * @Date 2020-05-22
 */
@RestController
@RequestMapping("testSysInterfaceLog")
@Slf4j
public class SysInterfaceLogTestController  extends BaseController {

    @Autowired
    public ISysInterfaceLogTestService sysInterfaceLogTestService;

    @PostMapping("saveTest")
    @ApiOperation(value = "新增保存接口调用日志表", response = SysInterfaceLog.class)
    public R saveInterfaceLogTest(@RequestBody SysInterfaceLog sysInterfaceLog){
        return sysInterfaceLogTestService.addSave(sysInterfaceLog);
    }
}
