package com.cloud.system.feign.factory;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    @Override
    public RemoteUserService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteUserService() {
            @Override
            public SysUser selectSysUserByUsername(String username) {
                log.error(StrUtil.format("RemoteUserFallbackFactory.selectSysUserByUsername错误信息：",throwable.getMessage()));
                return null;
            }

            @Override
            public R updateUserLoginRecord(SysUser user) {
                log.error(StrUtil.format("RemoteUserFallbackFactory.updateUserLoginRecord错误信息：",throwable.getMessage()));
                return R.error();
            }

            @Override
            public SysUser selectSysUserByUserId(long userId) {
                SysUser user = new SysUser();
                user.setUserId(0l);
                user.setLoginName("no user");
                return user;
            }

            @Override
            public Set<Long> selectUserIdsHasRoles(String roleId) {
                log.error(StrUtil.format("RemoteUserFallbackFactory.selectUserIdsHasRoles错误信息：",throwable.getMessage()));
                return null;
            }

            @Override
            public Set<Long> selectUserIdsInDepts(String deptIds) {
                log.error(StrUtil.format("RemoteUserFallbackFactory.selectUserIdsInDepts错误信息：",throwable.getMessage()));
                return null;
            }

            /**
             * 根据供应商V码查询供应商信息
             * @param supplierCode 供应商编号
             * @return 用户信息
             */
            @Override
            public R findUserBySupplierCode(String supplierCode) {
                log.error("RemoteUserService.findUserBySupplierCode error:{}",throwable.getMessage());
                return R.error("服务拥挤，请稍后再试！");
            }
            /**
             * Description:  查询用户权限
             * Param: []
             * return: com.cloud.common.core.domain.R
             * Author: ltq
             * Date: 2020/6/19
             */
            @Override
            public R selectUserRights(String roleKey) {
                log.error("查询用户权限失败，原因{}："+throwable.getMessage());
                return R.error("查询用户权限失败，原因{}："+throwable.getMessage());
            }

            @Override
            public R selectUserByMaterialCodeAndRoleKey(String materialCode, String roleKey) {
                log.error("根据工厂或物料号 角色查 用户信息失败,原因{}："+throwable.getMessage());
                return R.error("根据工厂或物料号,角色查用户信息失败,原因{}："+throwable.getMessage());
            }
        };
    }
}
