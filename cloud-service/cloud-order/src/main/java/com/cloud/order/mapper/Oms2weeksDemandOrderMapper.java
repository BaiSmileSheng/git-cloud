package com.cloud.order.mapper;

import com.cloud.order.domain.entity.Oms2weeksDemandOrder;
import com.cloud.common.core.dao.BaseMapper;
/**
 * T+1-T+2周需求 Mapper接口
 *
 * @author cs
 * @date 2020-06-12
 */
public interface Oms2weeksDemandOrderMapper extends BaseMapper<Oms2weeksDemandOrder>{
    int deleteAll();
}
