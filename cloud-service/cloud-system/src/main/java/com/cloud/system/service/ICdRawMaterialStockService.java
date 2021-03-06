package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 原材料库存 Service接口
 *
 * @author ltq
 * @date 2020-06-05
 */
public interface ICdRawMaterialStockService extends BaseService<CdRawMaterialStock> {
    /**
     * @Description: 导出原材料库存报表
     * @Param: [cdRawMaterialStock]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/9
     */
    R exportRawMaterialExcel(CdRawMaterialStock cdRawMaterialStock);

    /**
     * 删除全部数据
     * @return
     */
    R deleteAll();
    /**
     * Description:  根据List<CdRawMaterialStock>查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/28
     */
    R selectByList(List<CdRawMaterialStock> list);

    /**
     * 实时获取原材料库存信息
     * @param list
     * @param sysUser
     * @return
     */
    R currentqueryRawMaterialStock(List<CdRawMaterialStock> list, SysUser sysUser);
}
