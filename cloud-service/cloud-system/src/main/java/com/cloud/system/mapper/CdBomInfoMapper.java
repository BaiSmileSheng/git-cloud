package com.cloud.system.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdBomInfo;
import org.apache.ibatis.annotations.MapKey;

import java.util.Map;

/**
 * bom清单数据 Mapper接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface CdBomInfoMapper extends BaseMapper<CdBomInfo>{

    /**
     * 根据物料号工厂分组取bom版本
     * @return
     */
    @MapKey("keyValue")
    Map<String,Map<String, String>> selectVersionMap();
}
