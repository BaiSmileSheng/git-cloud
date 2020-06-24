package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.common.core.service.BaseService;

/**
 * 排产订单明细 Service接口
 *
 * @author ltq
 * @date 2020-06-19
 */
public interface IOmsProductionOrderDetailService extends BaseService<OmsProductionOrderDetail> {
    /**
     * Description:  根据orderCodes 查询
     * Param: [orderCodes]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    R selectListByOrderCodes(String orderCodes);

    int delectByProductOrderCode(String productOrderCode);

}
