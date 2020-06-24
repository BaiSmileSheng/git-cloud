package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.common.core.service.BaseService;

import java.util.List;

/**
 * 成品库存在库明细 Service接口
 *
 * @author lihongxia
 * @date 2020-06-12
 */
public interface ICdProductWarehouseService extends BaseService<CdProductWarehouse> {
    /**
     * 删除全表
     * @return
     */
    R deleteAll();

    R selectByList(List<CdProductWarehouse> list);
}
