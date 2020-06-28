package com.cloud.order.service.impl;

    import com.cloud.common.core.domain.R;
    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsProductionOrderDetailMapper;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.order.service.IOmsProductionOrderDetailService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 排产订单明细 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-19
 */
@Service
public class OmsProductionOrderDetailServiceImpl extends BaseServiceImpl<OmsProductionOrderDetail> implements IOmsProductionOrderDetailService {
    @Autowired
    private OmsProductionOrderDetailMapper omsProductionOrderDetailMapper;
    /**
     * Description:  根据orderCodes 查询
     * Param: [orderCodes]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    @Override
    public R selectListByOrderCodes(String orderCodes) {
        return R.data(omsProductionOrderDetailMapper.selectListByOrderCodes(orderCodes));
    }
    /**
     * Description:根据排产订单号删除明细数据
     * Param: [productOrderCode]
     * return: int
     * Author: ltq
     * Date: 2020/6/24
     */
    @Override
    public int delectByProductOrderCode(String productOrderCode) {
        return omsProductionOrderDetailMapper.deleteByProductOrderCode(productOrderCode);
    }
}
