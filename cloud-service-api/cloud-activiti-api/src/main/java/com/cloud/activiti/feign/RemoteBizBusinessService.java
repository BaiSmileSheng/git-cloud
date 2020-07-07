package com.cloud.activiti.feign;

import com.cloud.activiti.feign.factory.RemoteBizBusinessFallbackFactory;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description:  流程业务
 * Param:
 * return:
 * Author: lihongxia
 * Date: 2020/7/6
 */
@FeignClient(name = ServiceNameConstants.ACTIVITI_SERVICE, fallbackFactory = RemoteBizBusinessFallbackFactory.class)
public interface RemoteBizBusinessService {

    /**
     * 根据procDefKey和tableId查procInstId
     * @param procDefKey
     * @param tableId
     * @return
     */
    @GetMapping("business/selectByKeyAndTable")
    R selectByKeyAndTable(@RequestParam("procDefKey") String procDefKey,@RequestParam("tableId") String tableId);
}
