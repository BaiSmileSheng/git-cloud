package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * T+1-T+2周需求导入 Service接口
 *
 * @author cs
 * @date 2020-06-22
 */
public interface IOms2weeksDemandOrderEditService extends BaseService<Oms2weeksDemandOrderEdit> {
    /**
     * T+1、T+2草稿计划导入
     *
     * @param file
     * @param sysUser
     * @return
     */
    R import2weeksDemandEdit(MultipartFile file, SysUser sysUser);

    /**
     * T+1、T+2草稿计划汇总
     * @param successList 成功结果集
     * @param auditList 需要审核的结果集
     * @return
     */
    R import2weeksDemandEdit(List<Oms2weeksDemandOrderEdit> successList, List<Oms2weeksDemandOrderEdit> auditList, SysUser sysUser);

    /**
     * 根据创建人和客户编码删除
     * @param createBy
     * @param customerCodes
     * @return
     */
    int deleteByCreateByAndCustomerCode(String createBy,List<String> customerCodes);


    /**
     * 带逻辑更新
     * @param oms2weeksDemandOrderEdit
     * @return
     */
    R updateWithLimit(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit);

    /**
     * 带逻辑删除
     * @param ids
     * @return
     */
    R deleteWithLimit(String ids);

    /**
     * 确认下达
     * @param ids
     * @return
     */
    R confirmRelease(String ids);

    /**
     * 查询不重复的物料号和工厂
     * @param oms2weeksDemandOrderEdit
     * @return
     */
    R selectDistinctMaterialCodeAndFactoryCode(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit, SysUser sysUser);
    /**
     * T+1、T+2草稿计划对比分析分页查询
     * @param listDistant 不重复的物料和工厂
     * @return
     */
    R t1t2GatherList(List<Oms2weeksDemandOrderEdit> listDistant);

    /**
     * T+1、T+2草稿计划对比分析 导出
     * @param oms2weeksDemandOrderEdit
     * @param sysUser
     * @return
     */
    R t1t2GatherListExport(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit, SysUser sysUser);

    /**
     * 下达SAP
     * @param ids
     * @return
     */
    R toSAP(List<Long> ids,SysUser sysUser);

    /**
     * 根据需求订单号批量更新
     * @param list
     * @return
     */
    int updateBatchByDemandOrderCode(List<Oms2weeksDemandOrderEdit> list);

    /**
     * SAP601创建订单接口定时任务（ZPP_INT_DDPS_02）
     * @return
     */
    R queryPlanOrderCodeFromSap601();

}
