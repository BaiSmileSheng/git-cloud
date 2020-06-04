package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.feign.factory.RemoteCdMaterialPriceInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
     * @param materialCode
     * @param beginDate
     * @param endDate
     * @return List<CdMaterialPriceInfo>
     */
    @GetMapping("materialPrice/findByMaterialCode")
    List<CdMaterialPriceInfo> findByMaterialCode(@RequestParam(value = "materialCode") String materialCode,
                                                 @RequestParam(value = "beginDate") String beginDate,
                                                 @RequestParam(value = "endDate") String endDate);


    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     * @param materialCode
     * @return R CdMaterialPriceInfo对象
     */
    @PostMapping("materialPrice/checkSynchroSAP")
    R checkSynchroSAP(@RequestParam(value = "materialCode") String materialCode);

    /**
     * 校验申请数量是否是最小包装量的整数倍
     * @param materialCode applyNum
     * @return R
     */
    @PostMapping("materialPrice/checkIsMinUnit")
    R checkIsMinUnit(@RequestParam(value = "materialCode") String materialCode,@RequestParam(value = "applyNum") int applyNum);
}
