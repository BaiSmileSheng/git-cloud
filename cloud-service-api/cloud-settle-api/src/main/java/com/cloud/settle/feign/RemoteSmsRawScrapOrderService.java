package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.feign.factory.RemoteSmsRawScrapOrderFallbackFactory;
import com.cloud.settle.feign.factory.RemoteSmsScrapOrderFallbackFactory;
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
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteSmsRawScrapOrderFallbackFactory.class)
public interface RemoteSmsRawScrapOrderService {
    /**
     * 根据创建时间查询原材料报废申请
     * @param createTimeStart
     * @param endTimeStart
     * @return
     */
    @GetMapping("rawMaterialScrapOrder/listByTime")
    R listByTime(@RequestParam("createTimeStart") String createTimeStart,@RequestParam("endTimeStart") String endTimeStart);

    /**
     * 定时任务更新价格
     * @param
     * @return
     */
    @PostMapping("rawMaterialScrapOrder/updateRawScrapJob")
    public R updateRawScrapJob();

    /**
     * 查询原材料报废申请
     */
    @GetMapping("rawMaterialScrapOrder/get")
    R get(@RequestParam(value = "id") Long id);

    /**
     * 审核通过传SAP系统261进行报废
     * @param
     * @return
     */
    @PostMapping("rawMaterialScrapOrder/autidSuccessToSAP261")
    R autidSuccessToSAP261(@RequestBody SmsRawMaterialScrapOrder smsRawMaterialScrapOrder);

    /**
     * 更新原材料报废
     * @param
     * @return
     */
    @PostMapping("rawMaterialScrapOrder/updateAct")
    R updateAct(@RequestBody SmsRawMaterialScrapOrder smsRawMaterialScrapOrder);

}
