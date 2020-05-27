package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 加工费结算
 *@Author Lihongxia
 * @Date 2020-05-26
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE)
public interface RemoteSettleInfoService {

    /**
     * 查询加工费结算 列表
     * @param smsSettleInfo 订单结算信息--加工费结算信息
     * @return TableDataInfo 加工费结算分页列表
     */
    @GetMapping("/smsSettleInfo/list")
    TableDataInfo list(@RequestParam("smsSettleInfo") SmsSettleInfo smsSettleInfo);

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
}
