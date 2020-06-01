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
     * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
     * @return 成功或失败
     */
    R batchAddDelaysDelivery();

}
