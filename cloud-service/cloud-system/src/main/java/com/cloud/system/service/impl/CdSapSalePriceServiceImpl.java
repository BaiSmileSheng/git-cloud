package com.cloud.system.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.mapper.CdSapSalePriceMapper;
import com.cloud.system.service.ICdSapSalePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 成品销售价格 Service业务层处理
 *
 * @author cs
 * @date 2020-06-03
 */
@Service
public class CdSapSalePriceServiceImpl extends BaseServiceImpl<CdSapSalePrice> implements ICdSapSalePriceService {
    @Autowired
    private CdSapSalePriceMapper cdSapSalePriceMapper;


    }
