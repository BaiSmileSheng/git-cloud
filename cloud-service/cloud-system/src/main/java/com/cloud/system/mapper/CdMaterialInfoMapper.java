package com.cloud.system.mapper;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdMaterialInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 物料信息 Mapper接口
 *
 * @author ltq
 * @date 2020-06-01
 */
public interface CdMaterialInfoMapper extends BaseMapper<CdMaterialInfo> {
    int updateBatchByFactoryAndMaterial(List<CdMaterialInfo> list);

    /**
     * 根据物料号集合查询物料信息
     *
     * @param materialCodes
     * @return
     */
    List<CdMaterialInfo> selectInfoByInMaterialCodeAndMaterialType(@Param(value = "materialCodes") List<String> materialCodes, @Param("materialType") String materialType);


    List<CdMaterialInfo> selectListByMaterialList(@Param(value = "list") List<Dict> list);
}
