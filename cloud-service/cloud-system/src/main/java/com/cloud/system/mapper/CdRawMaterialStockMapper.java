package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 原材料库存 Mapper接口
 *
 * @author ltq
 * @date 2020-06-05
 */
public interface CdRawMaterialStockMapper extends BaseMapper<CdRawMaterialStock>{

    /**
     * 删除全部数据
     * @return
     */
    int deleteAll();
    /**
     * 根据多个生产工厂、原材料物料查询
     * @return List<CdRawMaterialStock>
     */
    List<CdRawMaterialStock> selectByList(@Param(value = "list") List<CdRawMaterialStock> list);

}
