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
    R saveMaterialInfo();
    R materialInfoInterface(List<RowRisk> list, int page, String batchId);
    }
