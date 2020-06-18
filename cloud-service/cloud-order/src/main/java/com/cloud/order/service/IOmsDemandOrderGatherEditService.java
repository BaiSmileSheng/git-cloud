package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.system.domain.entity.SysUser;

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
    R deleteWithLimit(String ids);

    /**
     * 确认下达
     * @param ids
     * @return
     */
    R confirmRelease(String ids);


    /**
     * 需求数据导入
     * @param successList 成功结果集
     * @param auditList 需要审核的结果集
     * @return
     */
    R importDemandGatherEdit(List<OmsDemandOrderGatherEdit> successList,List<OmsDemandOrderGatherEdit> auditList, SysUser sysUser);


    /**
     * 根据创建人和客户编码删除
     * @param createBy
     * @param customerCode
     * @return
     */
	int deleteByCreateByAndCustomerCode(String createBy,List<String> customerCodes);


}
