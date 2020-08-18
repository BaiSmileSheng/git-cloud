package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 滚动计划需求操作 Service接口
 *
 * @author cs
 * @date 2020-06-16
 */
public interface IOmsDemandOrderGatherEditService extends BaseService<OmsDemandOrderGatherEdit> {

    /**
     * 带逻辑更新
     * @param omsDemandOrderGatherEdit
     * @return
     */
    R updateWithLimit(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit);

    /**
     * 带逻辑删除
     * @param ids
     * @return
     */
    R deleteWithLimit(String ids,OmsDemandOrderGatherEdit omsDemandOrderGatherEdit,SysUser sysUser);

    /**
     * 确认下达
     * @param ids
     * @return
     */
    R confirmRelease(String ids,OmsDemandOrderGatherEdit omsDemandOrderGatherEdit,SysUser sysUser);


    /**
     * 需求数据汇总
     * @param successList 成功结果集
     * @param auditList 需要审核的结果集
     * @return
     */
    R importDemandGatherEdit(List<OmsDemandOrderGatherEdit> successList,List<OmsDemandOrderGatherEdit> auditList, SysUser sysUser);


    /**
     * 根据创建人和客户编码删除
     * @param createBy
     * @param customerCodes
     * @return
     */
	int deleteByCreateByAndCustomerCode(String createBy,List<String> customerCodes,String status);

    /**
     * 需求数据导入
     * @param file
     * @param sysUser
     * @return
     */
    R importDemandGatherEdit(MultipartFile file,SysUser sysUser);


    /**
     * 13周滚动需求汇总分页查询
     * @param listDistant 不重复的物料和工厂
     * @return
     */
    R week13DemandGatherList(List<OmsDemandOrderGatherEdit> listDistant);

    /**
     * 查询不重复的物料号和工厂
     * @param omsDemandOrderGatherEdit
     * @return
     */
    List<OmsDemandOrderGatherEdit> selectDistinctMaterialCodeAndFactoryCode(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit,SysUser sysUser);

    /**
     * 13周滚动需求汇总 导出
     * @param omsDemandOrderGatherEdit
     * @param sysUser
     * @return
     */
    R week13DemandGatherExport(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit,SysUser sysUser);


    /**
     * 下达SAP(13周需求下达SAP创建生产订单)
     * @param ids
     * @return
     */
    R toSAP(List<Long> ids,SysUser sysUser,OmsDemandOrderGatherEdit omsDemandOrderGatherEdit);


    /**
     * 根据需求订单号批量更新
     * @param list
     * @return
     */
	int updateBatchByDemandOrderCode(List<OmsDemandOrderGatherEdit> list);

}
