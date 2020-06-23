package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;

import java.util.List;

/**
 * 物料扩展信息 Service接口
 *
 * @author lihongia
 * @date 2020-06-15
 */
public interface ICdMaterialExtendInfoService extends BaseService<CdMaterialExtendInfo> {

    /**
     * 定时任务传输成品物料接口
     *
     * @return
     */
    R timeSycMaterialCode();


    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
	R selectMaterialCodeByLifeCycle(String lifeCycle);

    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    R selectInfoInMaterialCodes(List<String> materialCodes);
}
