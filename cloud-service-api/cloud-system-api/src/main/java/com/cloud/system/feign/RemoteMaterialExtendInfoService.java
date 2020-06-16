package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteMaterialExtendInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 物料扩展信息  Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-16
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteMaterialExtendInfoFallbackFactory.class)
public interface RemoteMaterialExtendInfoService {

    /**
     * 定时任务传输成品物料接口
     *
     * @return
     */
    @PostMapping("materialExtendInfo/timeSycMaterialCode")
    R timeSycMaterialCode();
}
