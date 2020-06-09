package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdFactoryLineInfo;

/**
 * 工厂线体关系 Service接口
 *
 * @author cs
 * @date 2020-06-01
 */
public interface ICdFactoryLineInfoService extends BaseService<CdFactoryLineInfo> {

    /**
     * 根据供应商编号查询线体
     * @param supplierCode
     * @return 逗号分隔线体编号
     */
    R selectLineCodeBySupplierCode(String supplierCode);

    /**
     * 根据线体查询信息
     * @param produceLineCode
     * @return 供应商编码
     */
    CdFactoryLineInfo selectInfoByCodeLineCode(String produceLineCode);
    /**
     * @Description: 获取SAP系统工厂线体关系数据，保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    R saveFactoryLineInfo();

}
