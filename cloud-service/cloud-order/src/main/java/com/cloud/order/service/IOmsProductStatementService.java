package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductStatement;
import com.cloud.common.core.service.BaseService;

/**
 * T-1交付考核报 Service接口
 *
 * @author lihongxia
 * @date 2020-08-07
 */
public interface IOmsProductStatementService extends BaseService<OmsProductStatement> {

    /**
     * 定时汇总T-1交付考核报
     */
    R timeAddSave();
}
