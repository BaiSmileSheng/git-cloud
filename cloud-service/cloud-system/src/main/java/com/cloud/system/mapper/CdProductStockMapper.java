package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.common.core.dao.BaseMapper;

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

    List<CdProductStock> selectByList(List<CdProductStock> list);
}
