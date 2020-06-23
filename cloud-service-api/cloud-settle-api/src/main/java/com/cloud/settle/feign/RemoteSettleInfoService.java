package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.feign.factory.RemoteSettleInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 加工费结算
 *@Author Lihongxia
 * @Date 2020-05-26
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE,fallbackFactory = RemoteSettleInfoFallbackFactory.class)
public interface RemoteSettleInfoService {

    /**
     * 修改保存加工费结算
     * @param smsSettleInfo 加工费结算信息
     * @return R 修改成功或失败
     */
    @PostMapping("/smsSettleInfo/update")
    R editSave(@RequestBody SmsSettleInfo smsSettleInfo);


    /**
     * 新增保存加工费结算
     * @param smsSettleInfo 加工费结算信息
     * @return R 修改成功或失败
     */
    @PostMapping("/smsSettleInfo/save")
    R addSave(@RequestBody SmsSettleInfo smsSettleInfo);

    /**
     * 计算加工费(定时任务调用)
     * @return 成功或失败
     */
    @PostMapping("/smsSettleInfo/smsSettleInfoCalculate")
    R smsSettleInfoCalculate();
}
