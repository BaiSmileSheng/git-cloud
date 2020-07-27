package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActStartProcessVo;
import com.cloud.common.core.domain.R;

import java.util.List;

/**
 * Description:  排产订单审批流程
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/23
 */
public interface IActOmsProductionOrderService {

    /**
     * 审批流程 排产订单审批流程推进逻辑
     * @param bizAudit
     * @param userId
     * @return R
     */
    R audit(BizAudit bizAudit, long userId);

    /**
     * 根据业务key获取数据
     * @param businessKey
     * @return smsSupplementaryOrder
     * @author cs
     */
    R getBizInfoByTableId(String businessKey);

    /**
     * Description:  开启审批流
     * Param: [key, orderId, orderCode, userId, title]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    R startActProcess(ActBusinessVo actBusinessVo);

}
