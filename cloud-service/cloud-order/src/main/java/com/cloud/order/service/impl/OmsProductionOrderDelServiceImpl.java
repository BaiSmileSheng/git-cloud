package com.cloud.order.service.impl;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsProductionOrderDelMapper;
import com.cloud.order.domain.entity.OmsProductionOrderDel;
import com.cloud.order.service.IOmsProductionOrderDelService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 排产订单删除 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-22
 */
@Service
public class OmsProductionOrderDelServiceImpl extends BaseServiceImpl<OmsProductionOrderDel> implements IOmsProductionOrderDelService {
    @Autowired
    private OmsProductionOrderDelMapper omsProductionOrderDelMapper;


    }
