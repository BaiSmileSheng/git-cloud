package com.cloud.settle.mapper;

import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 质量报废Mapper接口
 *
 * @author ltq
 * @date 2020-12-10
 */
public interface SmsQualityScrapOrderMapper extends BaseMapper<SmsQualityScrapOrder>{
    /**
     * 根据月份和状态查询物料号
     * @param month
     * @param scrapStatus
     * @return
     */
    List<String> selectMaterialByMonthAndStatus(@Param("month") String month, @Param("scrapStatus") List<String> scrapStatus);

    /**
     * 根据月份和状态查询
     * @param month
     * @param scrapStatus
     * @return
     */
    List<SmsQualityScrapOrder> selectByMonthAndStatus(@Param("month") String month, @Param("scrapStatus") List<String> scrapStatus);

    /**
     * 查询指定月份应更新的销售价格的专用号和公司
     * @param month
     * @param scrapStatus
     * @return
     */
    List<Map<String,String>> selectMaterialAndCompanyCodeGroupBy(@Param("month") String month, @Param("scrapStatus") List<String> scrapStatus);

}
