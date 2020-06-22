package com.cloud.system.mapper;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdProductStock;

import java.util.List;

/**
 * 成品库存主 Mapper接口
 *
 * @author lihongxia
 * @date 2020-06-12
 */
public interface CdProductStockMapper extends BaseMapper<CdProductStock>{
    /**
     * 删除全表
     * @return
     */
    int deleteAll();

    /**
     * 根据工厂，专用号分组取成品库存
     * @param dicts
     * @return
     */
    List<CdProductStock> selectProductStockToMap(List<Dict> dicts);
}
