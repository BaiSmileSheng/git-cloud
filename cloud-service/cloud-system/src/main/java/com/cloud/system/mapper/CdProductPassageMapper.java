package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.common.core.dao.BaseMapper;

import java.util.List;

/**
 * 成品库存在途明细 Mapper接口
 *
 * @author lihongxia
 * @date 2020-06-12
 */
public interface CdProductPassageMapper extends BaseMapper<CdProductPassage>{
    /**
     * 删除全表
     * @return
     */
    int deleteAll();
    /**
     * 根据生产工厂、成品专用号、库位查询在途
     * @return list
     */
    List<CdProductPassage> selectByList(List<CdProductPassage> list);
}
