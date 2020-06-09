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
     * 查询质量索赔详情
     * @param id 主键id
     * @return 质量索赔信息详情(包含文件信息)
     */
    @GetMapping("qualityOrder/selectById")
    R selectById(@RequestParam("id") Long id);


    /**
     * 修改保存质量索赔
     * @param smsQualityOrder 质量索赔信息
     * @return 修改成功或失败
     */
    @PostMapping("qualityOrder/editSave")
    R editSave(@RequestBody SmsQualityOrder smsQualityOrder);

    /**
     * 48H超时未确认发送邮件
     * @return 成功或失败
     */
    @PostMapping("qualityOrder/overTimeSendMail")
    R overTimeSendMail();

    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    @PostMapping("qualityOrder/overTimeConfim")
    R overTimeConfim();
}
