package com.cloud.activiti.feign;

import com.cloud.activiti.feign.factory.RemoteActTaskFallbackFactory;
import com.cloud.activiti.feign.factory.RemoteBizBusinessFallbackFactory;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Description:  流程业务
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/8/12
 */
@FeignClient(name = ServiceNameConstants.ACTIVITI_SERVICE, fallbackFactory = RemoteActTaskFallbackFactory.class)
public interface RemoteActTaskService {

    /**
     * Description:  根据业务订单号删除审批流程
     * Param: [orderCodeList]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/8/12
     */
    @PostMapping("task/deleteByOrderCode")
    R deleteByOrderCode(@RequestBody Map<String,Object> map);
}
