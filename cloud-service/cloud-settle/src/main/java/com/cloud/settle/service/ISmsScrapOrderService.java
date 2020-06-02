package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsScrapOrder;

/**
 * 报废申请 Service接口
 *
 * @author cs
 * @date 2020-05-29
 */
public interface ISmsScrapOrderService extends BaseService<SmsScrapOrder> {

    /**
     * 编辑报废申请单功能  --有状态校验
     * @param smsScrapOrder
     * @return
     */
    R editSave(SmsScrapOrder smsScrapOrder);

    /**
     * 删除报废申请
     * @param ids
     * @return
     */
    R remove(String ids);
}
