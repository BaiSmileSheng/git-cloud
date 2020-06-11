package com.cloud.system.service;

import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdSapSalePrice;

import java.util.List;
import java.util.Map;

/**
 * 成品销售价格 Service接口
 *
 * @author cs
 * @date 2020-06-03
 */
public interface ICdSapSalePriceService extends BaseService<CdSapSalePrice> {

    /**
     * 根据专用号和销售组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode+organization,CdSapSalePrice>
     */
    Map<String, CdSapSalePrice> selectPriceByInMaterialCodeAndDate(List<String> materialCodes,
                                                                        String beginDate,
                                                                        String endDate);


    /**
     * 根据销售组织、专用号更新数据
     * @param updated
     * @param marketingOrganization
     * @param materialCode
     * @return
     */
	int updateByMarketingOrganizationAndMaterialCode(CdSapSalePrice updated,String marketingOrganization,String materialCode);

}
