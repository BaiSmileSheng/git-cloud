package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdBomInfo;

/**
 * bom清单数据 Service接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface ICdBomInfoService extends BaseService<CdBomInfo> {
    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R 单耗
     */
    R checkBomNum(String productMaterialCode, String rawMaterialCode, int applyNum);

    /**
     * @Description: 定时任务获取BOM清单-保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    R saveBomInfoBySap();
}
