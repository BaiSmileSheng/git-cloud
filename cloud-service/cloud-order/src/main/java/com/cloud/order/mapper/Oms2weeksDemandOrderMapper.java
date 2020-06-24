package com.cloud.order.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.Oms2weeksDemandOrder;

import java.util.List;

/**
 * T+1-T+2周需求 Mapper接口
 *
 * @author cs
 * @date 2020-06-12
 */
public interface Oms2weeksDemandOrderMapper extends BaseMapper<Oms2weeksDemandOrder>{
    int deleteAll();

    /**
     * 根据物料号和工厂查询
     * @param list
     * @return
     */
    List<Oms2weeksDemandOrder> selectInfoInMaterialCodeAndFactoryCode(List<Oms2weeksDemandOrder> list);

}
