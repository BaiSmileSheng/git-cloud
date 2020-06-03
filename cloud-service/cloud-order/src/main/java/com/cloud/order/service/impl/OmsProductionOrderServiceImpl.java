package com.cloud.order.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.mapper.OmsProductionOrderMapper;
import com.cloud.order.service.IOmsProductionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 排产订单 Service业务层处理
 *
 * @author cs
 * @date 2020-05-29
 */
@Service
public class OmsProductionOrderServiceImpl extends BaseServiceImpl<OmsProductionOrder> implements IOmsProductionOrderService {
    @Autowired
    private OmsProductionOrderMapper omsProductionOrderMapper;


}
