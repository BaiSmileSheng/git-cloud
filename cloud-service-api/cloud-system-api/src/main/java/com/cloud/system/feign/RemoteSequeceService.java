package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.system.feign.factory.RemoteSequeceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 获取序列号Feign服务层
 *
 * @author Lihongxia
 * @date 2020-06-02
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSequeceFallbackFactory.class)
public interface RemoteSequeceService {

    /**
     * 获取序列号
     * @param name 序列名称
     * @param length 所需序列号长度
     * @return 序列号
     */
    @GetMapping("sequece/selectSeq")
    String selectSeq(@RequestParam("name") String name, @RequestParam("length") int length);
}
