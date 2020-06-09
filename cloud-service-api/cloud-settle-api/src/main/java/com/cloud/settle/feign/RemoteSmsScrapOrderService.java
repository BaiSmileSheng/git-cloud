package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.feign.factory.RemoteSmsScrapOrderFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 报废管理 Feign服务层
 *
 * @author cs
 * @date 2020-05-20
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteSmsScrapOrderFallbackFactory.class)
public interface RemoteSmsScrapOrderService {
    /**
     * 根据ID查询报废管理申请表
     * @param id
     * @return SmsScrapOrder
     */
    @GetMapping("scrapOrder/get")
    SmsScrapOrder get(@RequestParam("id") Long id);

    /**
     * 修改保存报废管理申请  -- 无状态校验
     * @param smsScrapOrder
     * @return 是否成功
     */
    @PostMapping("scrapOrder/update")
    R update(@RequestBody SmsScrapOrder smsScrapOrder);

    /**
     * 修改保存报废管理申请  --有状态校验
     * @param smsScrapOrder
     * @return 是否成功
     */
    @PostMapping("scrapOrder/editSave")
    R editSave(@RequestBody SmsScrapOrder smsScrapOrder);

    /**
     * 新增保存报废申请
     * @param smsScrapOrder
     * @return 是否成功
     */
    @PostMapping("scrapOrder/save")
    R addSave(@RequestBody SmsScrapOrder smsScrapOrder);

    /**
     * 定时任务更新指定月份销售价格到报废表
     * @param month
     * @return
     */
    @GetMapping("scrapOrder/updatePriceEveryMonth")
    R updatePriceEveryMonth(@RequestParam("month") String month);
}
