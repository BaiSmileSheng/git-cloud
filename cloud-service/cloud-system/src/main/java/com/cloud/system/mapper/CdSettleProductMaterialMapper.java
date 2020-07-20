package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.common.core.dao.BaseMapper;

import java.util.List;

/**
 * 物料号和加工费号对应关系 Mapper接口
 *
 * @author cs
 * @date 2020-06-05
 */
public interface CdSettleProductMaterialMapper extends BaseMapper<CdSettleProductMaterial> {

    /**
     * 批量查询
     * @param list
     * @return
     */
    List<CdSettleProductMaterial> batchSelect(List<CdSettleProductMaterial> list);

    /**
     * 批量新增
     * @param list
     * @return
     */
    int batchInsertOrUpdate(List<CdSettleProductMaterial> list);

}
