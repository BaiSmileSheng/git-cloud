package com.cloud.system.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdSapSalePrice;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 成品销售价格 Mapper接口
 *
 * @author cs
 * @date 2020-06-03
 */
public interface CdSapSalePriceMapper extends BaseMapper<CdSapSalePrice>{
    /**
     * 根据专用号和销售组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return
     */
    @MapKey("getVKey")
    Map<String, CdSapSalePrice> selectPriceByInMaterialCodeAndDate(@Param(value = "materialCodes") List<String> materialCodes,
                                                                        @Param(value = "beginDate") String beginDate,
                                                                        @Param(value = "endDate") String endDate);

    /**
     * 根据销售组织、专用号更新数据
     * @param updated
     * @param marketingOrganization
     * @param materialCode
     * @return
     */
    int updateByMarketingOrganizationAndMaterialCode(@Param("updated")CdSapSalePrice updated,@Param("marketingOrganization")String marketingOrganization,@Param("materialCode")String materialCode);


}
