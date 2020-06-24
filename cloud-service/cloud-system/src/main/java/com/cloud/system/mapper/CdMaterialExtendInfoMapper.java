package com.cloud.system.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 物料扩展信息 Mapper接口
 *
 * @author lihongia
 * @date 2020-06-15
 */
public interface CdMaterialExtendInfoMapper extends BaseMapper<CdMaterialExtendInfo>{

    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
    List<String> selectMaterialCodeByLifeCycle(@Param("lifeCycle")String lifeCycle);


    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    @MapKey("materialCode")
    Map<String, CdMaterialExtendInfo> selectInfoInMaterialCodes(@Param(value = "materialCodes") List<String> materialCodes);
}
