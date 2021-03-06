package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.system.domain.entity.SysUser;

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
    R addSave(SmsSupplementaryOrder smsSupplementaryOrder, SysUser sysUser);

    /**
     * 新增保存物耗申请单(多条)
     * @param smsSupplementaryOrders
     * @return id
     */
    R addSaveList(List<SmsSupplementaryOrder> smsSupplementaryOrders, SysUser sysUser);

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

    /**
     * 根据状态查物料号
     * @param status
     * @return 物料号集合
     */
    List<String> materialCodeListByStatus(String status);
}
