package com.cloud.system.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdSapSalePrice;
import com.cloud.system.mapper.CdSapSalePriceMapper;
import com.cloud.system.service.ICdSapSalePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据专用号和销售组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode+organization,CdSapSalePrice>
     */
    @Override
    public Map<String, CdSapSalePrice> selectPriceByInMaterialCodeAndDate(List<String> materialCodes, String beginDate, String endDate) {
        return cdSapSalePriceMapper.selectPriceByInMaterialCodeAndDate(materialCodes,beginDate,endDate);
    }
}
