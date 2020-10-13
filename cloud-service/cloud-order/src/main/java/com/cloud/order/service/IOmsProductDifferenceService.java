package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductDifference;
import com.cloud.common.core.service.BaseService;

/**
 * 外单排产差异报表 Service接口
 *
 * @author ltq
 * @date 2020-09-30
 */
public interface IOmsProductDifferenceService extends BaseService<OmsProductDifference>{
    /**
     * Description: 定时任务汇总外单排产差异报表 ，每周六执行
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/9/30
     */
    R timeProductDiffTask();

    }
