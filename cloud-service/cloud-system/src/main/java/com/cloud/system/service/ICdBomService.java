package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdBom;

/**
 * bom清单数据 Service接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface ICdBomService extends BaseService<CdBom> {
    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R 单耗
     */
    R checkBomNum(String productMaterialCode, String rawMaterialCode, int applyNum);
}
