package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.common.core.dao.BaseMapper;

import java.util.List;

/**
 * 物料信息 Mapper接口
 *
 * @author ltq
 * @date 2020-06-01
 */
public interface CdMaterialInfoMapper extends BaseMapper<CdMaterialInfo>{
    int updateBatchByFactoryAndMaterial(List<CdMaterialInfo> list);
}
