package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.feign.factory.RemoteSmsSupplementaryOrderFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 物耗管理 Feign服务层
 *
 * @author cs
 * @date 2020-05-20
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteSmsSupplementaryOrderFallbackFactory.class)
public interface RemoteSmsSupplementaryOrderService {
    /**
     * 根据ID查询物耗管理申请表
     * @param id
     * @return SmsSupplementaryOrder
     */
    @GetMapping("supplementary/get")
    SmsSupplementaryOrder get(@RequestParam("id") Long id);

    /**
     * 修改保存物耗管理申请  -- 无状态校验
     * @param smsSupplementaryOrder
     * @return 是否成功
     */
    @PostMapping("supplementary/update")
    R update(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder);

    /**
     * 修改保存物耗管理申请  --有状态校验
     * @param smsSupplementaryOrder
     * @return 是否成功
     */
    @PostMapping("supplementary/editSave")
    R editSave(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder);

    /**
     * 新增保存物耗申请单
     * @param smsSupplementaryOrder
     * @return 是否成功
     */
    @PostMapping("supplementary/save")
    R addSave(@RequestBody SmsSupplementaryOrder smsSupplementaryOrder);
}
