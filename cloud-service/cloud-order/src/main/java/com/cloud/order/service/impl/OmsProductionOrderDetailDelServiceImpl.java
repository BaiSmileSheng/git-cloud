package com.cloud.order.service.impl;

    import com.cloud.common.core.domain.R;
    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsProductionOrderDetailDelMapper;
import com.cloud.order.domain.entity.OmsProductionOrderDetailDel;
import com.cloud.order.service.IOmsProductionOrderDetailDelService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 排产订单明细删除 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-22
 */
@Service
public class OmsProductionOrderDetailDelServiceImpl extends BaseServiceImpl<OmsProductionOrderDetailDel> implements IOmsProductionOrderDetailDelService {
    @Autowired
    private OmsProductionOrderDetailDelMapper omsProductionOrderDetailDelMapper;

}
