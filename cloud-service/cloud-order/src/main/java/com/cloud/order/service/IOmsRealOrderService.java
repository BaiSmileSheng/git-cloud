package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 真单Service接口
 *
 * @author ltq
 * @date 2020-06-15
 */
public interface IOmsRealOrderService extends BaseService<OmsRealOrder> {

    /**
     * 修改保存真单
     * @param omsRealOrder 真单对象
     * @return
     */
    R editSaveOmsRealOrder(OmsRealOrder omsRealOrder, SysUser sysUser,long userId);

    /**
     * 导入真单
     * @param successResult  需要导入的数据
     * @param auditResult  需要审核的数据
     * @param sysUser  用户信息
     * @param orderFrom  内单或外单
     * @return
     */
    R importOmsRealOrder(List<OmsRealOrder> successResult,List<OmsRealOrder> auditResult,SysUser sysUser,String orderFrom);

    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     * @return
     */
    R timeCollectToOmsRealOrder();
}
