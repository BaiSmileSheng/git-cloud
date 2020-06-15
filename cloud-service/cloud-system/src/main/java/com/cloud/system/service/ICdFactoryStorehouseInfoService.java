package com.cloud.system.service;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;

import java.util.List;
import java.util.Map;

/**
 * 工厂库位 Service接口
 *
 * @author cs
 * @date 2020-06-15
 */
public interface ICdFactoryStorehouseInfoService extends BaseService<CdFactoryStorehouseInfo> {

    /**
     * 根据工厂，客户编码分组取接收库位
     * @param dicts
     * @return
     */
    Map<String, Map<String, String>> selectStorehouseToMap(List<Dict> dicts);
}
