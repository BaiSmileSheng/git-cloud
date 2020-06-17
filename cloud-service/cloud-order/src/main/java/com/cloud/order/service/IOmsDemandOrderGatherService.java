package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsDemandOrderGather;

/**
 * 滚动计划需求 Service接口
 *
 * @author cs
 * @date 2020-06-12
 */
public interface IOmsDemandOrderGatherService extends BaseService<OmsDemandOrderGather> {

    /**
     * 周五需求数据汇总
     * @return
     */
    R gatherDemandOrderFriday();

    /**
     * 周一需求数据汇总
     * @return
     */
    R gatherDemandOrderMonday();
}
