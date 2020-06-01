package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.feign.factory.RemoteCdMaterialPriceInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

/**
 * 用户 Feign服务层
 *
 * @author cs
 * @date 2020-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCdMaterialPriceInfoFallbackFactory.class)
public interface RemoteCdMaterialPriceInfoService {
    /**
     * 根据Example条件查询列表
     * @param example
     * @return List<CdMaterialPriceInfo>
     */
    @GetMapping("materialPrice/example")
    R findByExample(Example example);


    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     * @param materialCode
     * @return R CdMaterialPriceInfo对象
     */
    @PostMapping("materialPrice/checkSynchroSAP")
    R checkSynchroSAP(@RequestParam(value = "materialCode") String materialCode);
}
