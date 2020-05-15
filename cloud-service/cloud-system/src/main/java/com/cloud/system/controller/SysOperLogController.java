package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.SysOperLog;
import com.cloud.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志记录 提供者
 *
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("operLog")
public class SysOperLogController extends BaseController {
    @Autowired
    private ISysOperLogService sysOperLogService;

    /**
     * 查询操作日志记录
     */
    @GetMapping("get/{operId}")
    public SysOperLog get(@PathVariable("operId") Long operId) {
        return sysOperLogService.selectOperLogById(operId);
    }

    /**
     * 查询操作日志记录列表
     */
    @HasPermissions("monitor:operlog:list")
    @RequestMapping("list")
    public R list(SysOperLog sysOperLog) {
        startPage();
        return result(sysOperLogService.selectOperLogList(sysOperLog));
    }

    @OperLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @HasPermissions("monitor:operlog:export")
    @PostMapping("/export")
    public R export(SysOperLog operLog) {
        List<SysOperLog> list = sysOperLogService.selectOperLogList(operLog);
        return EasyExcelUtil.writeExcel(list,"操作日志.xlsx","sheet",new SysOperLog());
    }

    /**
     * 新增保存操作日志记录
     */
    @PostMapping("save")
    public void addSave(@RequestBody SysOperLog sysOperLog) {
        sysOperLogService.insertOperlog(sysOperLog);
    }

    /**
     * 删除操作日志记录
     */
    @HasPermissions("monitor:operlog:remove")
    @PostMapping("remove")
    public R remove(String ids) {
        return toAjax(sysOperLogService.deleteOperLogByIds(ids));
    }

    @OperLog(title = "操作日志", businessType = BusinessType.CLEAN)
    @HasPermissions("monitor:operlog:remove")
    @PostMapping("/clean")
    public R clean() {
        sysOperLogService.cleanOperLog();
        return R.ok();
    }
}
