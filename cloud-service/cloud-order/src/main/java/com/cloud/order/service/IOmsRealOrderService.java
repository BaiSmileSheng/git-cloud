package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.common.core.service.BaseService;

/**
 * 真单Service接口
 *
 * @author ltq
 * @date 2020-06-15
 */
public interface IOmsRealOrderService extends BaseService<OmsRealOrder> {











    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     * @return
     */
    R timeCollectToOmsRealOrder();
}
