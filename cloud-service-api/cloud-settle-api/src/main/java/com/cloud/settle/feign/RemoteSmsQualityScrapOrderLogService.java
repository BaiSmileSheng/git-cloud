package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.settle.feign.factory.RemoteSmsQualityScrapOrderFallbackFactory;
import com.cloud.settle.feign.factory.RemoteSmsQualityScrapOrderLogFallbackFactory;
import io.swagger.annotations.ApiOperation;
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
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteSmsQualityScrapOrderLogFallbackFactory.class)
public interface RemoteSmsQualityScrapOrderLogService {
    /**
     * 根据报废ID查询质量部报废申诉记录
     */
    @GetMapping("getByQualityId")
    public R getByQualityId(@RequestParam(value = "qualityId") Long qualityId);

}
