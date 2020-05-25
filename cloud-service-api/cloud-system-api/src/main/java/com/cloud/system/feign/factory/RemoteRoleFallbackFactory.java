package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import org.springframework.stereotype.Component;

import com.cloud.system.domain.entity.SysRole;
import com.cloud.system.feign.RemoteRoleService;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RemoteRoleFallbackFactory implements FallbackFactory<RemoteRoleService> {/* (non-Javadoc)
 * @see feign.hystrix.FallbackFactory#create(java.lang.Throwable)
 */

    @Override
    public RemoteRoleService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteRoleService() {
            @Override
            public SysRole selectSysRoleByRoleId(long roleId) {
                return null;
            }

            @Override
            public R addSave(SysRole sysRole) {
                return null;
            }
        };
    }
}
