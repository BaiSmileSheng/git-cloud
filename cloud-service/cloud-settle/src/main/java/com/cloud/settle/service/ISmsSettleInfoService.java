package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsSettleInfo;

import java.util.List;

/**
 * 加工费结算 Service接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface ISmsSettleInfoService extends BaseService<SmsSettleInfo> {

    /**
     * 计算加工费(定时任务调用)
     * @return 成功或失败
     */
    R smsSettleInfoCalculate();


    /**
     * 根据供应商编码、付款公司、订单状态、月份（基本开始日期）更新数据
     * @param updated
     * @param supplierCode
     * @param companyCode
     * @param orderStatus
     * @param month
     * @return
     */
	int updateBySupplierCodeAndCompanyCodeAndOrderStatusAndMonth(SmsSettleInfo updated,String supplierCode,String companyCode,String orderStatus,String month);

    /**
     * 根据供应商和付款公司分组，计算加工费
     *
     * @param month
     * @param orderStatus
     * @return
     */
    List<SmsSettleInfo> selectForMonthSettle(String month, String orderStatus);

    /**
     * 费用结算单明细（打印用）
     * @param smsSettleInfo
     * @return
     */
    R selectInfoForPrint(SmsSettleInfo smsSettleInfo);



}
