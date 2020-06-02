package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;

/**
 * SAP成本价格 Service接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface ICdMaterialPriceInfoService extends BaseService<CdMaterialPriceInfo> {

    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     * @param materialCode
     * @return
     */
    R checkSynchroSAP(String materialCode);

    /**
     * 校验申请数量是否是最小包装量的整数倍
     * @param materialCode
     * @param applyNum
     * @return
     */
    R checkIsMinUnit(String materialCode, int applyNum);
}
