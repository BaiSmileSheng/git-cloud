package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdMouthRate;

/**
 * 汇率Service接口
 *
 * @author cs
 * @date 2020-05-27
 */
public interface ICdMouthRateService extends BaseService<CdMouthRate> {

    /**
     * 新增
     * @param cdMouthRate
     * @return
     */
    R insertMouthRate(CdMouthRate cdMouthRate);
    /**
     *修改
     * @param cdMouthRate
     * @return
     */
    R updateMouthRate(CdMouthRate cdMouthRate);

}
