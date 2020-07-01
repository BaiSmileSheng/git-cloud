package com.cloud.order.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.order.feign.factory.RemoteDemandOrderGatherEditFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 滚动计划需求操作  Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-30
 */
@FeignClient(name = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = RemoteDemandOrderGatherEditFallbackFactory.class)
public interface RemoteDemandOrderGatherEditService {

    /**
     * 查询 滚动计划需求操作
     * @param id
     * @return
     */
    @GetMapping("demandOrderGatherEdit/get")
    R get(@RequestParam("id") Long id);

    /**
     * 修改 滚动计划需求操作
     * @param omsDemandOrderGatherEdit
     * @return
     */
    @PostMapping("demandOrderGatherEdit/updateGatherEdit")
    R updateGatherEdit(@RequestBody OmsDemandOrderGatherEdit omsDemandOrderGatherEdit);
}
