package com.cloud.activiti.service;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 真单审核工作流
 * @Author Lihongxia
 * @Date 2020-06-22
 */
public interface IActOmsOrderMaterialOutService {

    /**
     * 根据业务key获取下市审核信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 下市审核信息
     */
    R getBizInfoByTableId(String businessKey);

    /**
     * 物料下市审核 真单审核开启流程
     * @return 成功或失败
     */
    R addSave(OmsOrderMaterialOutVo omsOrderMaterialOutVo);

    /**
     * 下市流程审批
     * @param bizAudit
     * @param sysUser 当前用户信息
     * @return
     */
    R audit(BizAudit bizAudit, SysUser sysUser);


}
