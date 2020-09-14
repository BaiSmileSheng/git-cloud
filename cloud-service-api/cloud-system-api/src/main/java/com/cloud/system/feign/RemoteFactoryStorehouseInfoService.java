package com.cloud.system.feign;

import cn.hutool.core.lang.Dict;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.feign.factory.RemoteFactoryStorehouseInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 工厂库位 Feign服务层
 * @author cs
 * @date 2020-06-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteFactoryStorehouseInfoFallbackFactory.class)
public interface RemoteFactoryStorehouseInfoService {


    /**
     * 查询一个工厂库位
     * @param cdFactoryStorehouseInfo
     * @return
     */
    @GetMapping("factoryStorehouse/findOneByExample")
    R findOneByExample(@RequestParam(value = "cdFactoryStorehouseInfo") CdFactoryStorehouseInfo cdFactoryStorehouseInfo);

    /**
     * 根据条件查询
     */
    @GetMapping("factoryStorehouse/findByExample")
    R findByExample(@RequestParam(value = "cdFactoryStorehouseInfo") CdFactoryStorehouseInfo cdFactoryStorehouseInfo);


    /**
     * 根据工厂，客户编码分组取接收库位
     * @param dicts
     * @return
     */
    @PostMapping("factoryStorehouse/selectStorehouseToMap")
    R selectStorehouseToMap(@RequestBody List<Dict> dicts);

    /**
     * 查询工厂库位 列表
     * @param cdFactoryStorehouseInfoReq
     * @return
     */
    @GetMapping("factoryStorehouse/listFactoryStorehouseInfo")
    R listFactoryStorehouseInfo(@RequestParam("cdFactoryStorehouseInfoReq") String cdFactoryStorehouseInfoReq);
}
