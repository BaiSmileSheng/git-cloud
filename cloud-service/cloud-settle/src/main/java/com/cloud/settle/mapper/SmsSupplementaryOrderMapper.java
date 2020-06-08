package com.cloud.settle.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 物耗申请单 Mapper接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface SmsSupplementaryOrderMapper extends BaseMapper<SmsSupplementaryOrder> {

    /**
     * 根据月份和状态查询
     * @param month
     * @param stuffStatus
     * @return
     */
    List<SmsSupplementaryOrder> selectByMonthAndStatus(@Param("month") String month,@Param("stuffStatus") List<String> stuffStatus);

    /**
     * 根据月份和状态查询物料号
     * @param month
     * @param stuffStatus
     * @return
     */
    List<String> selectMaterialByMonthAndStatus(@Param("month") String month,@Param("stuffStatus") List<String> stuffStatus);

}
