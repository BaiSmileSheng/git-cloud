package com.cloud.settle.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 报废申请 Mapper接口
 *
 * @author cs
 * @date 2020-05-29
 */
public interface SmsScrapOrderMapper extends BaseMapper<SmsScrapOrder>{
    /**
     * 根据月份和状态查询
     * @param month
     * @param scrapStatus
     * @return
     */
    List<SmsScrapOrder> selectByMonthAndStatus(@Param("month") String month, @Param("scrapStatus") List<String> scrapStatus);

    /**
     * 根据月份和状态查询物料号
     * @param month
     * @param scrapStatus
     * @return
     */
    List<String> selectMaterialByMonthAndStatus(@Param("month") String month,@Param("scrapStatus") List<String> scrapStatus);


    /**
     * 查询指定月份应更新的销售价格的专用号和公司
     * @param month
     * @param scrapStatus
     * @return
     */
    List<Map<String,String>> selectMaterialAndCompanyCodeGroupBy(@Param("month") String month, @Param("scrapStatus") List<String> scrapStatus);

}
