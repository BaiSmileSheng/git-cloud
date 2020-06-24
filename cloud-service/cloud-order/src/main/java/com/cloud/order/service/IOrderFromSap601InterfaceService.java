package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;

import java.util.List;
/**
 * @Description: Order服务 - sap601系统接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/4
 */
public interface IOrderFromSap601InterfaceService {
    /**
     * @Description: 获取SAP系统生产订单
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/4
     */
    R queryProductOrderFromSap601(List<OmsProductionOrder> list);
    /**
     * @Description: 创建生产订单
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/4
     */
    R createProductOrderFromSap601(List<OmsProductionOrder> list);
}
