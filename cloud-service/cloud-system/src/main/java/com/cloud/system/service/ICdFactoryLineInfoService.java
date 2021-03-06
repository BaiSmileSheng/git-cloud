package com.cloud.system.service;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdFactoryLineInfo;

import java.util.List;
import java.util.Map;

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
     * @param factoryCode
     * @return 供应商编码
     */
    CdFactoryLineInfo selectInfoByCodeLineCode(String produceLineCode,String factoryCode);
    /**
     * @Description: 获取SAP系统工厂线体关系数据，保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    R saveFactoryLineInfo();
    /**
     * Description:  根据List<Map<String,String>>工厂、线体查询线体信息
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    R selectListByMapList(List<Dict> list);

}
