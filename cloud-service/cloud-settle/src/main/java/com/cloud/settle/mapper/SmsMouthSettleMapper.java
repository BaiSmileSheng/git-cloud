package com.cloud.settle.mapper;

import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.common.core.dao.BaseMapper;
/**
 * 月度结算信息 Mapper接口
 *
 * @author cs
 * @date 2020-06-04
 */
public interface SmsMouthSettleMapper extends BaseMapper<SmsMouthSettle>{

    /**
     * 月度结算  更新剩下的5个状态是11待结算索赔表状态为15
     * @return
     */
    int updateMouthSettleToUpdateStatus15();
}
