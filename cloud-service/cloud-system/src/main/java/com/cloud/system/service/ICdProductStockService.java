package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdProductStock;

/**
 * 成品库存主 Service接口
 *
 * @author lihongxia
 * @date 2020-06-12
 */
public interface ICdProductStockService extends BaseService<CdProductStock> {

    /**
     * 删除全表
     * @return
     */
    R deleteAll();
    /**
     * 同步成品库存
     * @param factoryCode 工厂编号
     * @param materialCode 物料编号
     * @return
     */
    R sycProductStock(String factoryCode,String materialCode);

    /**
     * 定时任务同步成品库存
     * @return
     */

    R timeSycProductStock();
}
