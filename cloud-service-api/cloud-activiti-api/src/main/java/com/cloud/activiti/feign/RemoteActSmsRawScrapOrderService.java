package com.cloud.activiti.feign;

import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.feign.factory.RemoteActOmsProductionOrderFallbackFactory;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Description:  排产订单审批流程
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/24
 */
@FeignClient(name = ServiceNameConstants.ACTIVITI_SERVICE, fallbackFactory = RemoteActOmsProductionOrderFallbackFactory.class)
public interface RemoteActSmsRawScrapOrderService {

    /**
     * 原材料报废审核开启流程  提交(编辑、新增提交)
     *
     * @param actBusinessVo
     * @return R 成功/失败
     */
    @PostMapping("actRawScrapOrder/open")
    R addSave(@RequestBody ActBusinessVo actBusinessVo);
}
