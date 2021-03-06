package com.cloud.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.annotation.LoginUser;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.RandomUtil;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.service.ISysMenuService;
import com.cloud.system.service.ISysUserService;
import com.cloud.system.util.PasswordUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户 提供者
 *
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysMenuService sysMenuService;

    /**
     * 查询用户
     */
    @GetMapping("get/{userId}")
    public SysUser get(@PathVariable("userId") Long userId) {
        return sysUserService.selectUserById(userId);
    }

    @GetMapping("info")
    public SysUser info(@LoginUser SysUser sysUser) {
        sysUser.setButtons(sysMenuService.selectPermsByUserId(sysUser.getUserId()));
        return sysUser;
    }

    /**
     * 查询用户
     */
    @GetMapping("find/{username}")
    public SysUser findByUsername(@PathVariable("username") String username) {
        return sysUserService.selectUserByLoginName(username);
    }

    /**
     * 查询拥有当前角色的所有用户
     */
    @GetMapping("hasRoles")
    public Set<Long> hasRoles(String roleIds) {
        Long[] arr = Convert.toLongArray(roleIds);
        return sysUserService.selectUserIdsHasRoles(arr);
    }

    /**
     * 查询所有当前部门中的用户
     */
    @GetMapping("inDepts")
    public Set<Long> inDept(String deptIds) {
        Long[] arr = Convert.toLongArray(deptIds);
        return sysUserService.selectUserIdsInDepts(arr);
    }

    /**
     * 查询用户列表
     */
    @GetMapping("list")
    public R list(SysUser sysUser) {
        startPage();
        return result(sysUserService.selectUserList(sysUser));
    }

    /**
     * 新增保存用户
     */
    @HasPermissions("system:user:add")
    @PostMapping("save")
    @OperLog(title = "用户管理", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody SysUser sysUser) {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(sysUserService.checkLoginNameUnique(sysUser.getLoginName()))) {
            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，登录账号已存在");
        } else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(sysUser))) {
            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，手机号码已存在");
        } else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(sysUserService.checkEmailUnique(sysUser))) {
            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，邮箱账号已存在");
        }
        sysUser.setSalt(RandomUtil.randomStr(6));
        sysUser.setPassword(
                PasswordUtil.encryptPassword(sysUser.getLoginName(), sysUser.getPassword(), sysUser.getSalt()));
        sysUser.setCreateBy(getLoginName());
        return toAjax(sysUserService.insertUser(sysUser));
    }

    /**
     * 修改保存用户
     */
    @HasPermissions("system:user:edit")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("update")
    public R editSave(@RequestBody SysUser sysUser) {
        if (null != sysUser.getUserId() && SysUser.isAdmin(sysUser.getUserId())) {
            return R.error("不允许修改超级管理员用户");
        } else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(sysUser))) {
            return R.error("修改用户'" + sysUser.getLoginName() + "'失败，手机号码已存在");
        } else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(sysUserService.checkEmailUnique(sysUser))) {
            return R.error("修改用户'" + sysUser.getLoginName() + "'失败，邮箱账号已存在");
        }
        return toAjax(sysUserService.updateUser(sysUser));
    }

    /**
     * 修改用户信息
     *
     * @param sysUser
     * @return
     * @author zmr
     */
    @HasPermissions("system:user:edit")
    @PostMapping("update/info")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R updateInfo(@RequestBody SysUser sysUser) {
        return toAjax(sysUserService.updateUserInfo(sysUser));
    }

    /**
     * 记录登陆信息
     *
     * @param sysUser
     * @return
     * @author zmr
     */
    @PostMapping("update/login")
    public R updateLoginRecord(@RequestBody SysUser sysUser) {
        return toAjax(sysUserService.updateUser(sysUser));
    }

    @HasPermissions("system:user:resetPwd")
    @OperLog(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public R resetPwdSave(@RequestBody SysUser user) {
        if (null != user.getUserId() && SysUser.isAdmin(user.getUserId())) {
            return R.error("不允许修改超级管理员用户");
        }
        user.setSalt(RandomUtil.randomStr(6));
        user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        return toAjax(sysUserService.resetUserPwd(user));
    }

    /**
     * 修改状态
     *
     * @param sysUser
     * @return
     * @author zmr
     */
    @HasPermissions("system:user:edit")
    @PostMapping("status")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R status(@RequestBody SysUser user) {
        if (null != user.getUserId() && SysUser.isAdmin(user.getUserId())) {
            return R.error("不允许修改超级管理员用户");
        }
        return toAjax(sysUserService.changeStatus(user));
    }

    /**
     * 删除用户
     *
     * @throws Exception
     */
    @HasPermissions("system:user:remove")
    @OperLog(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("remove")
    public R remove(String ids) throws Exception {
        return toAjax(sysUserService.deleteUserByIds(ids));
    }

    /**
     * 根据供应商V码查询供应商信息
     *
     * @param supplierCode 供应商编号
     * @return 用户信息
     */
    @ApiOperation(value = "根据供应商V码查询供应商信息 ", response = SysUser.class)
    @GetMapping("findUserBySupplierCode/{supplierCode}")
    public R findUserBySupplierCode(@PathVariable("supplierCode") String supplierCode) {
        SysUserVo sysUservo = sysUserService.findUserBySupplierCode(supplierCode);
        return R.data(sysUservo);
    }

    /**
     * Description:  查询用户权限
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/19
     */
    @PostMapping("selectUserRights")
    @ApiOperation(value = "查询用户权限 ", response = R.class)
    public R selectUserRights(@RequestBody String roleKey) {
        return sysUserService.selectUserRights(roleKey);
    }

    /**
     * 根据工厂或物料号 角色查 用户信息
     *
     * @param materialCode
     * @param roleKey
     * @return
     */
    @GetMapping("selectUserByMaterialCodeAndRoleKey")
    @ApiOperation(value = "根据工厂或物料号 角色查 用户信息 ", response = R.class)
    public R selectUserByMaterialCodeAndRoleKey(@RequestParam("materialCode") String materialCode, @RequestParam("roleKey") String roleKey) {
        return R.data(sysUserService.selectUserByMaterialCodeAndRoleKey(materialCode, roleKey));
    }

    /**
     * 根据角色、工厂、采购组查对应的用户信息
     *
     * @param factoryCode
     * @param purchaseCode
     * @param roleKey
     * @return
     */
    @GetMapping("selectUserByFactoryCodeAndPurchaseCodeAndRoleKey")
    @ApiOperation(value = "根据角色、工厂、采购组查对应的用户信息 ", response = R.class)
    public R selectUserByFactoryCodeAndPurchaseCodeAndRoleKey(@RequestParam("factoryCode") String factoryCode, @RequestParam("purchaseCode") String purchaseCode, @RequestParam("roleKey") String roleKey) {
        return R.data(sysUserService.selectUserByFactoryCodeAndPurchaseCodeAndRoleKey(factoryCode, purchaseCode, roleKey));
    }

    /**
     * 根据角色查对应的用户名,邮箱
     *
     * @param roleKey
     * @return
     */
    @GetMapping("selectUserByRoleKey")
    @ApiOperation(value = "根据角色查对应的用户名,邮箱", response = R.class)
    public R selectUserByRoleKey(@RequestParam("roleKey") String roleKey) {
        return R.data(sysUserService.selectUserByRoleKey(roleKey));
    }

    /**
     * 根据登录名查询用户
     *
     * @param
     * @return
     */
    @GetMapping("selectUserByLoginName")
    public R selectUserByLoginName(@RequestParam("loginNames") String loginNames) {
        Set<SysUserVo> sysUserSet = new HashSet<>();
        if (StrUtil.isNotBlank(loginNames)) {
            List<String> loginNameList = Arrays.asList(loginNames.split(","));
            loginNameList.forEach(n -> {
                        SysUser sysUser = sysUserService.selectUserByLoginName(n);
                        SysUserVo sysUserVo = BeanUtil.copyProperties(sysUser,
                                SysUserVo.class);

                        sysUserSet.add(sysUserVo);
                    }
            );
        }
        List<SysUserVo> sysUserList = new ArrayList<>(sysUserSet);
        return R.data(sysUserList);
    }

    /**
     * 查询所有有效的登录名
     * @return
     */
    @GetMapping("selectDistinctLoginName")
    public R selectDistinctLoginName() {
        List<String> list = sysUserService.selectDistinctLoginName();
        return R.data(list);
    }
}
