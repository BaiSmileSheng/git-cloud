package com.cloud.settle.mapper;

import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 原材料报废申请Mapper接口
 *
 * @author ltq
 * @date 2020-12-07
 */
public interface SmsRawMaterialScrapOrderMapper extends BaseMapper<SmsRawMaterialScrapOrder>{
    /**
     * 根据月份和状态查询原材料报废订单
     *
     * @author ltq
     * @date 2020-12-07
     */
    List<SmsRawMaterialScrapOrder> selectByMonthAndStatus(@Param(value = "lastMonth") String lastMonth, @Param(value = "scrapStatus") List<String> scrapStatus);
}
