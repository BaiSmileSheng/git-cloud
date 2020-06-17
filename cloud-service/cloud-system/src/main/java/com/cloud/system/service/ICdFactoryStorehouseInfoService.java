package com.cloud.system.service;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;

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

    /**
     * 根据工厂，客户编码分组取接收库位
     * @param dicts
     * @return
     */
    R selectStorehouseToMap(List<Dict> dicts);
}
