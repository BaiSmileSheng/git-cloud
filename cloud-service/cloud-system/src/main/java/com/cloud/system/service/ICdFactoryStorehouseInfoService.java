package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.common.core.service.BaseService;

import java.util.List;

/**
 * 工厂库位 Service接口
 *
 * @author cs
 * @date 2020-06-15
 */
public interface ICdFactoryStorehouseInfoService extends BaseService<CdFactoryStorehouseInfo> {

    /**
     * 批量新增或修改
     * @param list 工厂库位信息集合
     * @return  成功或失败
     */
    R batchInsertOrUpdate(List<CdFactoryStorehouseInfo> list);

}
