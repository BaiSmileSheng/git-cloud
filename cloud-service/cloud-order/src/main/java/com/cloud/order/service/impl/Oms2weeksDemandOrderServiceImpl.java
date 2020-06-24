package com.cloud.order.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.Oms2weeksDemandOrder;
import com.cloud.order.mapper.Oms2weeksDemandOrderMapper;
import com.cloud.order.service.IOms2weeksDemandOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * T+1-T+2周需求 Service业务层处理
 *
 * @author cs
 * @date 2020-06-12
 */
@Service
public class Oms2weeksDemandOrderServiceImpl extends BaseServiceImpl<Oms2weeksDemandOrder> implements IOms2weeksDemandOrderService {
    @Autowired
    private Oms2weeksDemandOrderMapper oms2weeksDemandOrderMapper;


    }
