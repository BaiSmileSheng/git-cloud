package com.cloud.system.feign;

import com.cloud.system.domain.entity.SysDept;
import com.cloud.system.feign.factory.RemoteDeptFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cloud.common.constant.ServiceNameConstants;

/**
 * 用户 Feign服务层
 *
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDeptFallbackFactory.class)
public interface RemoteDeptService {
    @GetMapping("dept/get/{deptId}")
    public SysDept selectSysDeptByDeptId(@PathVariable("deptId") long deptId);
}
