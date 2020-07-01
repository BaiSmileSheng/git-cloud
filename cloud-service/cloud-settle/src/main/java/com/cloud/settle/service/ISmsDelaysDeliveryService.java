package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;

/**
 * 延期交付索赔 Service接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface ISmsDelaysDeliveryService extends BaseService<SmsDelaysDelivery> {

    /**
     * 查询延期交付索赔详情
     * @param id 主键id
     * @return 延期交付索赔详情(包含文件信息)
     */
    R selectById(Long id);

    /**
     * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
     *
     * @return 成功或失败
     */
    R batchAddDelaysDelivery();

    /**
     * 延期索赔单供应商申诉(包含文件信息)
     * @param smsDelaysDeliveryReq 延期索赔信息
     * @return 延期索赔单供应商申诉结果成功或失败
     */
    R supplierAppeal(SmsDelaysDelivery smsDelaysDeliveryReq, String ossIds);


    /**
     * 供应商确认延期索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    R supplierConfirm(String ids);

    /**
     * 超时发送邮件
     * @return 成功或失败
     */
    R overTimeSendMail();

    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    R overTimeConfim();

}
