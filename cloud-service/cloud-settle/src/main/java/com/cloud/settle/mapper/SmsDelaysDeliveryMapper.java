package com.cloud.settle.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 延期交付索赔 Mapper接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface SmsDelaysDeliveryMapper extends BaseMapper<SmsDelaysDelivery>{
    List<SmsDelaysDelivery> selectByMonthAndStatus(@Param("month") String month, @Param("delaysStatus") List<String> delaysStatus);
}
