package com.cloud.settle.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 质量索赔 Mapper接口
 *
 * @author cs
 * @date 2020-05-27
 */
public interface SmsQualityOrderMapper extends BaseMapper<SmsQualityOrder>{

    List<SmsQualityOrder> selectByMonthAndStatus(@Param("month") String month, @Param("qualityStatus") List<String> qualityStatus);
}
