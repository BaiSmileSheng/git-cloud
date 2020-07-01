package com.cloud.system.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.service.ISysInterfaceLogService;
import io.seata.core.context.RootContext;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 接口调用日志  提供者
 * @author cs
 * @date 2020-05-20
 */
@RestController
@RequestMapping("interfaceLog")
public class SysInterfaceLogController extends BaseController {

    @Autowired
    private ISysInterfaceLogService sysInterfaceLogService;


    /**
     * 查询接口调用日志表
     * @param id 主键id
     * @return SysInterfaceLog 接口调用日志
     */
    @GetMapping("get")
    @ApiOperation(value = "根据主键id根据id查询接口调用日志表", response = SysInterfaceLog.class)
    public SysInterfaceLog get(Long id) {
        return sysInterfaceLogService.selectByPrimaryKey(id);

    }

    /**
     * 分页查询接口调用日志列表
     * @param sysInterfaceLog 接口调用日志
     * @return TableDataInfo  包含 List<SysInterfaceLog> 接口调用日志列表
     */
    @GetMapping("list")
    @ApiOperation(value = "接口调用日志表查询分页", response = SysInterfaceLog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SysInterfaceLog sysInterfaceLog) {
        Example example = new Example(SysInterfaceLog.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(sysInterfaceLog.getOrderCode())){
            criteria.andEqualTo("orderCode",sysInterfaceLog.getOrderCode());
        }
        startPage();
        List<SysInterfaceLog> sysInterfaceLogList = sysInterfaceLogService.selectByExample(example);
        return getDataTable(sysInterfaceLogList);
    }

    /**
     * 新增保存接口调用日志表
     * @param sysInterfaceLog 接口调用日志
     * @return R {"code":0,"msg":"success","data":"id的值"}
     */
    @PostMapping("save")
    @ApiOperation(value = "新增保存接口调用日志表", response = SysInterfaceLog.class)
    public R addSave(@RequestBody SysInterfaceLog sysInterfaceLog) {
        String unbindXid = RootContext.unbind();
        sysInterfaceLogService.insertUseGeneratedKeys(sysInterfaceLog);
        RootContext.bind(unbindXid);
        return R.data(sysInterfaceLog.getId());
    }

    /**
     * 修改保存接口调用日志表
     * @param sysInterfaceLog 接口调用日志
     * @return 修改结果,成功code:0或失败code:500
     */
    @PostMapping("update")
    @OperLog(title = "修改保存接口调用日志表", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存接口调用日志表", response = SysInterfaceLog.class)
    public R editSave(@RequestBody SysInterfaceLog sysInterfaceLog) {
        return toAjax(sysInterfaceLogService.updateByPrimaryKeySelective(sysInterfaceLog));
    }

    /**
     * 删除接口调用日志表
     * @param ids 主键id
     * @return 删除结果,成功code:0或失败code:500
     */
    @PostMapping("remove")
    @OperLog(title = "删除接口调用日志表", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除接口调用日志表", response = SysInterfaceLog.class)
    public R remove(String ids) {
        return toAjax(sysInterfaceLogService.deleteByIds(ids));
    }

}
