package com.cloud.system.controller;

import java.util.*;

import com.cloud.common.constant.Constants;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.service.ISysUserScopeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.system.domain.entity.SysUserScope;

/**
 * 用户和物料权限 提供者
 *
 * @author cs
 * @date 2020-05-02
 */
@RestController
@RequestMapping("userScope")
public class SysUserScopeController extends BaseController {

    @Autowired
    private ISysUserScopeService sysUserScopeService;

    @Autowired
    private RedisUtils redis;
    /**
     * 查询用户和物料权限
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询用户和物料权限", response = SysUserScope.class)
    public SysUserScope get(Long id) {
        return sysUserScopeService.selectByPrimaryKey(id);

    }

    /**
     * 查询用户和物料权限列表
     */
    @GetMapping("list")
    @ApiOperation(value = "用户和物料权限查询分页", response = SysUserScope.class)
    public R list(SysUserScope sysUserScope) {
        sysUserScope.setUserId(getCurrentUserId());
        List<SysUserScope> sysUserScopeList = sysUserScopeService.select(sysUserScope);
        return result(sysUserScopeList);
    }


    /**
     * 新增保存用户和物料权限
     */
    @PostMapping("save")
    @OperLog(title = "新增保存用户和物料权限", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存用户和物料权限", response = SysUserScope.class)
    public R addSave(@RequestBody SysUserScope sysUserScope) {
        return toAjax(sysUserScopeService.insertUseGeneratedKeys(sysUserScope));
    }

    /**
     * 修改保存用户和物料权限
     */
    @PostMapping("update")
    @OperLog(title = "修改保存用户和物料权限", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存用户和物料权限", response = SysUserScope.class)
    public R editSave(@RequestBody SysUserScope sysUserScope) {
        return toAjax(sysUserScopeService.updateByPrimaryKeySelective(sysUserScope));
    }

    /**
     * 删除用户和物料权限
     */
    @PostMapping("remove")
    @OperLog(title = "删除用户和物料权限", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除用户和物料权限", response = SysUserScope.class)
    public R remove(String ids) {
        return toAjax(sysUserScopeService.deleteByIds(ids));
    }

    /**
     * 查询用户所有有效权限
     *
     * @return
     */
    @GetMapping("getScopes")
    public String selectDataScopeIdByUserId(@RequestParam("userId") Long userId) {
        return sysUserScopeService.selectDataScopeIdByUserId(userId);
    }

    /**
     * 修改用户物料权限
     * @param sysUserScope
     * @return
     */
    @PostMapping("/updateUserScope")
    @OperLog(title = "修改用户物料权限", businessType = BusinessType.UPDATE)
    public R updateUserScope(@RequestBody SysUserScope sysUserScope) {
        if (sysUserScope.getUserId() == null) {
            return R.error("缺少参数用户Id");
        }
        String scopes= StringUtils.join(sysUserScope.getScopes(), ",");
        try {
            Long userId = sysUserScope.getUserId();
            //删除原有的
            sysUserScopeService.deleteByUserId(userId);
            //增加现有的
            if (sysUserScope.getScopes() != null) {
                List<SysUserScope> sysUserScopePoList = new ArrayList<>();
                for (String scope : sysUserScope.getScopes()) {
                    SysUserScope sysUserScopePoAdd = new SysUserScope().builder()
                            .userId(userId).dataScopeId(scope).build();
                    sysUserScopePoList.add(sysUserScopePoAdd);
                }
                sysUserScopeService.insertList(sysUserScopePoList);
            }
            //更新redis
            redis.set(Constants.ACCESS_USERID_SCOPE + userId, scopes, Constants.EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统错误！");
        }
        return R.ok();
    }
}
