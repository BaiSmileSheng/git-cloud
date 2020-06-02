package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;

/**
 * 物耗申请单 Service接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface ISmsSupplementaryOrderService extends BaseService<SmsSupplementaryOrder> {

    /**
     * 编辑保存物耗申请单功能  --有逻辑校验
     * @param smsSupplementaryOrder
     * @return
     */
    R editSave(SmsSupplementaryOrder smsSupplementaryOrder);

    /**
     * 删除物耗申请单
     * @param ids
     * @return
     */
    R remove(String ids);
}
