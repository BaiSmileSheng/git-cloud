package com.cloud.system.mapper;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdMouthRate;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 *  汇率Mapper接口
 *
 * @author cs
 * @date 2020-05-27
 */
public interface CdMouthRateMapper extends BaseMapper<CdMouthRate>{

    /**
     * 根据月份查询汇率
     * @param yearMouth
     * @return rate
     */
    BigDecimal findRateByYearMouth(@Param("yearMouth")String yearMouth);


}
