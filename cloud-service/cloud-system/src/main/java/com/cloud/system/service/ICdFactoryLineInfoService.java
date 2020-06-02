package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdFactoryLineInfo;

/**
 * 工厂线体关系 Service接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface ICdFactoryLineInfoService extends BaseService<CdFactoryLineInfo> {

    /**
     * 根据供应商编号查询线体
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    R selectLineCodeBySupplierCode(String supplierCode);

}
