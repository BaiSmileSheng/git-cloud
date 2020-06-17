package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.common.core.dao.BaseMapper;

import java.util.List;

/**
 * 工厂库位 Mapper接口
 *
 * @author cs
 * @date 2020-06-15
 */
public interface CdFactoryStorehouseInfoMapper extends BaseMapper<CdFactoryStorehouseInfo>{

    /**
     * 根据工厂编号和客户编号批量查询
     * @param listReq
     * @return
     */
    List<CdFactoryStorehouseInfo> batchSelectListByCondition(List<CdFactoryStorehouseInfo> listReq);
}
