package com.cloud.system.mapper;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import org.apache.ibatis.annotations.MapKey;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 工厂库位 Mapper接口
 *
 * @author cs
 * @date 2020-06-15
 */
public interface CdFactoryStorehouseInfoMapper extends BaseMapper<CdFactoryStorehouseInfo>{

    /**
     * 根据工厂，客户编码分组取接收库位
     * @param dicts
     * @return
     */
    @MapKey("keyValue")
    Map<String, Map<String, String>> selectStorehouseToMap(@RequestParam(value = "dicts") List<Dict> dicts);
}
