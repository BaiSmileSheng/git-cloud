package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.webService.material.RowRisk;

import java.util.List;

/**
 * 物料信息 Service接口
 *
 * @author ltq
 * @date 2020-06-01
 */
public interface ICdMaterialInfoService extends BaseService<CdMaterialInfo>{
    /**
     * @Description: 保存MDM接口获取的物料信息数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/5/29
     */
    R saveMaterialInfo();
    /**
     * @Description: 接口获取MDM物料信息
     * @Param: [list, pageAll, page, batchId]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/5/29
     */
    R materialInfoInterface(List<RowRisk> list, int page, String batchId);
    }
