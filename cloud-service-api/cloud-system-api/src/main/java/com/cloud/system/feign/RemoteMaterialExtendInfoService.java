package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteMaterialExtendInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
    @GetMapping("materialExtendInfo/selectMaterialCodeByLifeCycle")
    R selectMaterialCodeByLifeCycle(@RequestParam("lifeCycle") String lifeCycle);

    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    @PostMapping("materialExtendInfo/selectInfoInMaterialCodes")
    R selectInfoInMaterialCodes(@RequestBody List<String> materialCodes);
}
