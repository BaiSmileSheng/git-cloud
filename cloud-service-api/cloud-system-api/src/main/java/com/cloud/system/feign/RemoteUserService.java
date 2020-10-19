package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 用户 Feign服务层
 *
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {
    @GetMapping("user/get/{userId}")
    public SysUser selectSysUserByUserId(@PathVariable("userId") long userId);

    @GetMapping("user/find/{username}")
    public SysUser selectSysUserByUsername(@PathVariable("username") String username);

    @PostMapping("user/update/login")
    public R updateUserLoginRecord(@RequestBody SysUser user);

    /**
     * 查询拥有当前角色的所有用户
     *
     * @param auditor
     * @return
     * @author zmr
     */
    @GetMapping("user/hasRoles")
    public Set<Long> selectUserIdsHasRoles(@RequestParam("roleIds") String roleIds);

    /**
     * 查询所有当前部门中的用户
     *
     * @param deptId
     * @return
     * @author zmr
     */
    @GetMapping("user/inDepts")
    public Set<Long> selectUserIdsInDepts(@RequestParam("deptIds") String deptIds);

    /**
     * 根据供应商V码查询供应商信息
     * @param supplierCode 供应商编号
     * @return 用户信息
     */
    @GetMapping("user/findUserBySupplierCode/{supplierCode}")
    public R findUserBySupplierCode(@PathVariable("supplierCode") String supplierCode);
    /**
     * Description:  查询用户权限
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/19
     */
    @PostMapping("user/selectUserRights")
    R selectUserRights(@RequestBody String roleKey);

    /**
     * 根据工厂或物料号 角色查 用户信息
     * @param materialCode
     * @param roleKey
     * @return
     */
    @GetMapping("user/selectUserByMaterialCodeAndRoleKey")
    R selectUserByMaterialCodeAndRoleKey(@RequestParam("materialCode") String materialCode, @RequestParam("roleKey")String roleKey);

    /**
     * 根据角色、工厂、采购组查对应的用户信息
     * @param factoryCode
     * @param purchaseCode
     * @param roleKey
     * @return
     */
    @GetMapping("user/selectUserByFactoryCodeAndPurchaseCodeAndRoleKey")
    R selectUserByFactoryCodeAndPurchaseCodeAndRoleKey(@RequestParam("factoryCode") String factoryCode,@RequestParam("purchaseCode") String purchaseCode, @RequestParam("roleKey")String roleKey);


    /**
     * 根据角色查对应的用户名,邮箱
     * @param roleKey
     * @return
     */
    @GetMapping("user/selectUserByRoleKey")
    R selectUserByRoleKey(@RequestParam("roleKey")String roleKey);
    /**
     * 根据登录名查询用户
     * @param
     * @return
     */
    @GetMapping("user/selectUserByLoginName")
    R selectUserByLoginName(@RequestParam("loginNames")String loginNames);
}
