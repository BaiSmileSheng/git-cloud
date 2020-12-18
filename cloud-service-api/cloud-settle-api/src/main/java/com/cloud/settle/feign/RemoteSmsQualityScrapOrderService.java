package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.feign.factory.RemoteSmsQualityScrapOrderFallbackFactory;
import com.cloud.settle.feign.factory.RemoteSmsRawScrapOrderFallbackFactory;
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
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteSmsQualityScrapOrderFallbackFactory.class)
public interface RemoteSmsQualityScrapOrderService {
    /**
     * 查询原材料报废申请
     */
    @GetMapping("qualityScrapOrder/get")
    R get(@RequestParam(value = "id") Long id);

    /**
     * 更新原材料报废
     * @param
     * @return
     */
    @PostMapping("qualityScrapOrder/updateAct")
    R updateAct(@RequestBody SmsQualityScrapOrder smsQualityScrapOrder
            ,@RequestParam(value = "result") Integer result
            ,@RequestParam(value = "comment") String comment
            ,@RequestParam(value = "auditor") String auditor);

    /**
     * 定时更新质量部报废订单价格
     */
    @PostMapping("qualityScrapOrder/updatePriceJob")
    R updatePriceJob();

}
