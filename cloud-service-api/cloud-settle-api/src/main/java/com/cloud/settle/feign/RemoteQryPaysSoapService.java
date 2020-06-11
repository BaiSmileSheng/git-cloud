package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.feign.factory.RemoteQryPaysSoapFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 付款结果查询接口 Feign服务层
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteQryPaysSoapFallbackFactory.class)
public interface RemoteQryPaysSoapService {

    /**
     * 定时任务调用查询付款结果更新月度结算信息
     * @return
     */
    @PostMapping("qryPaysSoapController/updateKmsStatus")
    R updateKmsStatus();
}
