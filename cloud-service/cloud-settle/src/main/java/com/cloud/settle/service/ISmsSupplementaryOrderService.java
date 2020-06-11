package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;

import java.util.List;

/**
 * 物耗申请单 Service接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface ISmsSupplementaryOrderService extends BaseService<SmsSupplementaryOrder> {

    /**
     * 编辑保存物耗申请单功能  --有逻辑校验
     * @param smsSupplementaryOrder
     * @return
     */
    R editSave(SmsSupplementaryOrder smsSupplementaryOrder);

    /**
     * 编辑保存物耗申请单功能  --有逻辑校验（多条）
     * @param smsSupplementaryOrders
     * @return
     */
    R editSaveList(List<SmsSupplementaryOrder> smsSupplementaryOrders);

    /**
     * 删除物耗申请单
     * @param ids
     * @return
     */
    R remove(String ids);

    /**
     * 新增保存物耗申请单
     * @param smsSupplementaryOrder
     * @return id
     */
    R addSave(SmsSupplementaryOrder smsSupplementaryOrder);

    /**
     * 新增保存物耗申请单(多条)
     * @param smsSupplementaryOrders
     * @return id
     */
    R addSaveList(List<SmsSupplementaryOrder> smsSupplementaryOrders);

    /**
     * 根据月份和状态查询
     * @param month
     * @param stuffStatus
     * @return
     */
    List<SmsSupplementaryOrder> selectByMonthAndStatus(String month,List<String> stuffStatus);


    /**
     * 定时任务更新指定月份原材料价格到物耗表
     * @return
     */
    R updatePriceEveryMonth(String month);

    /**
     * 小微主审批通过传SAPY61
     * @param smsSupplementaryOrder
     * @return
     */
    R autidSuccessToSAPY61(SmsSupplementaryOrder smsSupplementaryOrder);
}
