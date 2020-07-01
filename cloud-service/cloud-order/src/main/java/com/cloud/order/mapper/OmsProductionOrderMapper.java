package com.cloud.order.mapper;

import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 排产订单 Mapper接口
 *
 * @author cs
 * @date 2020-05-29
 */
public interface OmsProductionOrderMapper extends BaseMapper<OmsProductionOrder>{
    /**
    * Description: 根据工厂、专用号、线体查询
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/6/23
    */

    List<OmsProductionOrder> selectByFactoryAndMaterialAndLine(@Param(value = "list") List<OmsProductionOrder> list);
    /**
    * Description: 根据LIst<id> 更新状态至待传SAP
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/6/23
    */

    int updateStatusByIds(@Param(value = "list") List<String> list);

    /**
     * 根据排产订单号批量修改
     * @param list
     * @return
     */
    int batchUpdateByOrderCode(List<OmsProductionOrder> list);
}
