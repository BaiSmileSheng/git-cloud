package com.cloud.system.mapper;

import cn.hutool.core.lang.Dict;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工厂线体关系 Mapper接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface CdFactoryLineInfoMapper extends BaseMapper<CdFactoryLineInfo> {

    /**
     * 根据供应商编号查询线体
     *
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    String selectLineCodeBySupplierCode(String supplierCode);

    /**
     * 根据线体查询信息
     *
     * @param produceLineCode
     * @param factoryCode
     * @return 供应商编码
     */
    CdFactoryLineInfo selectInfoByCodeLineCode(@Param("produceLineCode") String produceLineCode, @Param("factoryCode") String factoryCode);

    List<CdFactoryLineInfo> selectListByMapList(@Param(value = "list") List<Dict> list);
}
