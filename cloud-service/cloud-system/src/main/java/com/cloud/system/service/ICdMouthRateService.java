package com.cloud.system.service;

import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdMouthRate;

import java.math.BigDecimal;

/**
 * 汇率Service接口
 *
 * @author cs
 * @date 2020-05-27
 */
public interface ICdMouthRateService extends BaseService<CdMouthRate> {

    /**
     * 根据月份查询汇率
     * @param yearMouth
     * @return rate
     */
    BigDecimal findRateByYearMouth(String yearMouth);


}
