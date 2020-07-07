package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdSupplierInfo;
import com.cloud.system.feign.factory.RemoteSupplierInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户 Feign服务层
 * @author cs
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSupplierInfoFallbackFactory.class)
public interface RemoteSupplierInfoService {
    /**
     * 根据登录名查询供应商信息
     * @param loginName
     * @return CdSupplierInfo
     */
    @GetMapping("supplier/getByNick")
    CdSupplierInfo getByNick(@RequestParam(value = "loginName") String loginName);

    /**
     * 根据供应商编号查供应商信息
     * @param supplierCode
     * @return
     */
    @GetMapping("supplier/selectOneBySupplierCode")
    R selectOneBySupplierCode(@RequestParam("supplierCode") String supplierCode);
}
