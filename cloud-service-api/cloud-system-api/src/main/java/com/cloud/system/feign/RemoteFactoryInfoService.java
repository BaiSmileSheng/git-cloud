package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteFactoryInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 工厂信息 Feign服务层
 * @author cs
 * @date 2020-06-03
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteFactoryInfoFallbackFactory.class)
public interface RemoteFactoryInfoService {
    /**
     * 查询工厂信息
     * @param factoryCode
     * @return 工厂信息
     */
    @GetMapping("factoryInfo/getOne")
    R selectOneByFactory(@RequestParam(value = "factoryCode") String factoryCode);


    /**
     * 根据公司V码查询
     * @param companyCodeV
     * @return 工厂信息
     */
    @GetMapping("factoryInfo/selectAllByCompanyCodeV")
    R selectAllByCompanyCodeV(@RequestParam(value = "companyCodeV") String companyCodeV);

    /**
     * 获取所有公司编码
     * @return
     */
    @GetMapping("factoryInfo/getAllFactoryCode")
    R getAllFactoryCode();

    /**
     * 查询工厂信息 列表
     */
    @GetMapping("factoryInfo/listAll")
    R listAll();
}
