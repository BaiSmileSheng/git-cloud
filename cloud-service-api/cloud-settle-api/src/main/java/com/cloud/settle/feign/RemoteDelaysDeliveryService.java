package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.feign.factory.RemoteDelaysDeliveryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 延期索赔 提供者
 * @Author Lihongxia
 * @Date 2020-06-01
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE,fallbackFactory = RemoteDelaysDeliveryFallbackFactory.class)
public interface RemoteDelaysDeliveryService {
    /**
     * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
     * @return 成功或失败
     */
    @PostMapping("delaysDelivery/batchAddDelaysDelivery")
    R batchAddDelaysDelivery();

    /**
     * 查询延期交付索赔
     * @param id
     * @return 延期交付索赔信息
     */
    @GetMapping("delaysDelivery/get")
    SmsDelaysDelivery get(@RequestParam("id") Long id);

    /**
     * 查询延期交付索赔详情
     * @param id 主键id
     * @return 延期交付索赔详情(包含文件信息)
     */
    @GetMapping("delaysDelivery/selectById")
    R selectById(@RequestParam("id") Long id);

    /**
     * 修改延期索赔信息
     * @param smsDelaysDelivery 延期索赔信息
     * @return 修改数量
     */
    @PostMapping("delaysDelivery/update")
    R editSave(@RequestBody SmsDelaysDelivery smsDelaysDelivery);

    /**
     * 48H超时未确认发送邮件
     * @return 成功或失败
     */
    @PostMapping("delaysDelivery/overTimeSendMail")
    R overTimeSendMail();

    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    @PostMapping("delaysDelivery/overTimeConfim")
    R overTimeConfim();
}
