package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.common.core.service.BaseService;

import java.util.List;

/**
 * 物料号和加工费号对应关系 Service接口
 *
 * @author cs
 * @date 2020-06-05
 */
public interface ICdSettleProductMaterialService extends BaseService<CdSettleProductMaterial> {

    /**
     * 批量新增或修改(根据 product_material_code+raw_material_code唯一性修改)
     * @param list 物料号和加工费号对应关系集合
     * @return 成功或失败
     */
    R batchInsertOrUpdate(List<CdSettleProductMaterial> list);

}
