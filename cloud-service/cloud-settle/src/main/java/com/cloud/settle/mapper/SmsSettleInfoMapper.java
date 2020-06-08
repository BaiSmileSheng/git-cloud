package com.cloud.settle.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 加工费结算 Mapper接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface SmsSettleInfoMapper extends BaseMapper<SmsSettleInfo> {


    /**
     * 根据供应商和付款公司分组，计算加工费
     *
     * @param month
     * @param orderStatus
     * @return
     */
    List<SmsSettleInfo> selectForMonthSettle(@Param("month") String month, @Param("orderStatus") String orderStatus);

    /**
     * 根据供应商编码、付款公司、订单状态、月份（基本开始日期）更新数据
     * @param updated
     * @param supplierCode
     * @param componyCode
     * @param orderStatus
     * @param month
     * @return
     */
    int updateBySupplierCodeAndComponyCodeAndOrderStatusAndMonth(@Param("updated")SmsSettleInfo updated,@Param("supplierCode")String supplierCode,@Param("componyCode")String componyCode,@Param("orderStatus")String orderStatus,@Param("month") String month);


}
