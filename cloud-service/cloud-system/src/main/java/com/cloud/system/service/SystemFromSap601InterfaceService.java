package com.cloud.system.service;

import com.cloud.common.core.domain.R;

import java.util.List;

/**
 * @Description: System服务 - sap601系统接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/2
 */
public interface SystemFromSap601InterfaceService {
    /**
    * @Description: 获取uph数据
    * @Param:  factorys,materials
    * @return:
    * @Author: ltq
    * @Date: 2020/6/2
    */
    R queryUphFromSap601(List<String> factorys, List<String> materials);
    /**
     * @Description: 获取SAP系统工厂线体关系数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/2
     */
    R queryFactoryLineFromSap601();
    /**
     * @Description: 获取原材料库存接口
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    R queryRawMaterialStockFromSap601(List<String> factorys, List<String> materials);
    /**
     * @Description: 获取BOM清单数据
     * @Param: [factorys, materials]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    R queryBomInfoFromSap601(List<String> factorys, List<String> materials);

    /**
     * 定时获取BOM清单数据
     * @return
     */
    R sycBomInfo();

    /**
     * 定时同步原材料库存
     * @return
     */
    R sycRawMaterialStock();

}
