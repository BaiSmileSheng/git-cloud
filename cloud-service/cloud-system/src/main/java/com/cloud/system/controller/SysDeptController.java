package com.cloud.system.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysDept;
import com.cloud.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 部门 提供者
 *
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("dept")
public class SysDeptController extends BaseController {
    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询部门
     */
    @GetMapping("get/{deptId}")
    public SysDept get(@PathVariable("deptId") Long deptId) {
        return sysDeptService.selectDeptById(deptId);
    }

    /**
     * 查询部门列表
     */
    @GetMapping("list")
    public R list(SysDept sysDept) {
        startPage();
        return result(sysDeptService.selectDeptList(sysDept));
    }

    /**
     * 查询所有可用部门
     */
    @GetMapping("list/enable")
    public R listEnable(SysDept sysDept) {
        sysDept.setStatus("0");
        return result(sysDeptService.selectDeptList(sysDept));
    }

    /**
     * 新增保存部门
     */
    @PostMapping("save")
    public R addSave(@RequestBody SysDept sysDept) {
        return toAjax(sysDeptService.insertDept(sysDept));
    }

    /**
     * 修改保存部门
     */
    @PostMapping("update")
    public R editSave(@RequestBody SysDept sysDept) {
        return toAjax(sysDeptService.updateDept(sysDept));
    }

    /**
     * 删除部门
     */
    @PostMapping("remove/{deptId}")
    public R remove(@PathVariable("deptId") Long deptId) {
        return toAjax(sysDeptService.deleteDeptById(deptId));
    }

    /**
     * 加载角色部门（数据权限）列表树
     */
    @GetMapping("/role/{roleId}")
    public Set<String> deptTreeData(@PathVariable("roleId") Long roleId) {
        if (null == roleId || roleId <= 0) return null;
        return sysDeptService.roleDeptIds(roleId);
    }
}
