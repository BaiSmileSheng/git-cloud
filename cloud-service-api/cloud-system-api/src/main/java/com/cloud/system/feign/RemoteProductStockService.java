package com.cloud.system.feign;

import cn.hutool.core.lang.Dict;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.feign.factory.RemoteSapSystemInterfaceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 成品库存主表 Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-13
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSapSystemInterfaceFallbackFactory.class)
public interface RemoteProductStockService {
    /**
     * 定时任务同步成品库存
     *
     * @return
     */
    @PostMapping("productStock/timeSycProductStock")
    R timeSycProductStock();


    /**
     * 根据Example查询一条数据
     * @param cdProductStock
     * @return
     */
    @PostMapping("productStock/findOneByExample")
    R findOneByExample(@RequestBody CdProductStock cdProductStock);

    /**
     * 根据工厂，专用号分组取成品库存
     * @param dicts
     * @return
     */
    @PostMapping("productStock/selectProductStockToMap")
    R selectProductStockToMap(@RequestBody List<Dict> dicts);
}
