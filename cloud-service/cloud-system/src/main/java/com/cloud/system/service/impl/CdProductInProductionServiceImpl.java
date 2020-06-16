package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdProductInProductionMapper;
import com.cloud.system.domain.entity.CdProductInProduction;
import com.cloud.system.service.ICdProductInProductionService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 成品库存在产明细 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@Service
public class CdProductInProductionServiceImpl extends BaseServiceImpl<CdProductInProduction> implements ICdProductInProductionService {
    @Autowired
    private CdProductInProductionMapper cdProductInProductionMapper;

    /**
     * 删除全表
     * @return
     */
    @Override
    public R deleteAll() {
        cdProductInProductionMapper.deleteAll();
        return R.ok();
    }
}
