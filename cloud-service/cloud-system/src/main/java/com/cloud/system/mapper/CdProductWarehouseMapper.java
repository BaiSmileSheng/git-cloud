package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.common.core.dao.BaseMapper;
/**
 * 成品库存在库明细 Mapper接口
 *
 * @author lihongxia
 * @date 2020-06-12
 */
public interface CdProductWarehouseMapper extends BaseMapper<CdProductWarehouse>{
    /**
     * 删除全表
     * @return
     */
    int deleteAll();
}
