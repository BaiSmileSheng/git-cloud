package com.cloud.order.mapper;

import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 排产订单明细 Mapper接口
 *
 * @author ltq
 * @date 2020-06-19
 */
public interface OmsProductionOrderDetailMapper extends BaseMapper<OmsProductionOrderDetail>{

    List<OmsProductionOrderDetail> selectListByOrderCodes(@Param(value = "orderCodes") String orderCodes);

    int deleteByProductOrderCode(@Param("productOrderCode")String productOrderCode);


}
