package com.cloud.system.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * SAP成本价格 Mapper接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface CdMaterialPriceInfoMapper extends BaseMapper<CdMaterialPriceInfo> {
    @MapKey("materialCode")
    Map<String, CdMaterialPriceInfo> selectPriceByInMaterialCodeAndDate(@Param(value = "materialCodes") List<String> materialCodes,
                                                               @Param(value = "beginDate") String beginDate,
                                                               @Param(value = "endDate") String endDate);
}
