package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.feign.factory.RemoteQualityOrderFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 质量索赔 提供者
 *@Author Lihongxia
 * @Date 2020-05-26
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE,fallbackFactory = RemoteQualityOrderFallbackFactory.class)
public interface RemoteQualityOrderService {
    /**
     * 查询质量索赔
     * @param id 主键id
     * @return SmsQualityOrder 质量索赔信息
     */
    @GetMapping("qualityOrder/get")
    SmsQualityOrder get(@RequestParam("id") Long id);


    /**
     * 修改保存质量索赔
     * @param smsQualityOrder 质量索赔信息
     * @return 修改成功或失败
     */
    @PostMapping("qualityOrder/editSave")
    R editSave(@RequestBody SmsQualityOrder smsQualityOrder);
}
