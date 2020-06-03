package com.cloud.system.mapper;

import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.common.core.dao.BaseMapper;
/**
 * 工厂线体关系 Mapper接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface CdFactoryLineInfoMapper extends BaseMapper<CdFactoryLineInfo>{

    /**
     * 根据供应商编号查询线体
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    String selectLineCodeBySupplierCode(String supplierCode);

    /**
     * 根据线体查询信息
     * @param produceLineCode
     * @return 供应商编码
     */
    CdFactoryLineInfo selectInfoByCodeLineCode(String produceLineCode);
}
