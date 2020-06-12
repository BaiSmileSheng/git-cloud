package com.cloud.system.service;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdFactoryInfo;

import java.util.Map;

/**
 * 工厂信息 Service接口
 *
 * @author cs
 * @date 2020-06-03
 */
public interface ICdFactoryInfoService extends BaseService<CdFactoryInfo>{


    /**
     * 根据公司V码查询
     * @param companyCodeV
     * @return
     */
    Map<String, CdFactoryInfo> selectAllByCompanyCodeV(String companyCodeV);


}
