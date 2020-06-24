package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.feign.factory.RemoteInternalOrderResServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 真单 提供者 Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-18
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteInternalOrderResServiceFallbackFactory.class)
public interface RemoteOmsRealOrderService {
    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     */
    @PostMapping("realOrder/timeCollectToOmsRealOrder")
    R timeCollectToOmsRealOrder();

    /**
     * 查询真单
     */
    @GetMapping("realOrder/get")
    R get(@RequestParam("id") Long id);

    /**
     * 修改保存真单
     */
    @PostMapping("realOrder/update")
    R editSave(@RequestBody OmsRealOrder omsRealOrder);
}
