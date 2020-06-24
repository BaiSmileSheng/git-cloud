package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdProductWarehouseMapper;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.service.ICdProductWarehouseService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成品库存在库明细 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@Service
public class CdProductWarehouseServiceImpl extends BaseServiceImpl<CdProductWarehouse> implements ICdProductWarehouseService {
    @Autowired
    private CdProductWarehouseMapper cdProductWarehouseMapper;

    /**
     * 删除全表
     * @return
     */
    @Override
    public R deleteAll() {
        cdProductWarehouseMapper.deleteAll();
        return R.ok();
    }

    @Override
    public R selectByList(List<CdProductWarehouse> list) {
        return R.data(cdProductWarehouseMapper.selectByList(list));
    }
}
