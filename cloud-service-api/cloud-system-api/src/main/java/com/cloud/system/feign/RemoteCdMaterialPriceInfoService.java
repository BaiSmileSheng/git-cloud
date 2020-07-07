package com.cloud.system.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.feign.factory.RemoteCdMaterialPriceInfoFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
                                                 @RequestParam(value = "purchasingGroup") String purchasingGroup,
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
     * 根据物料号和采购组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode+organization,CdMaterialPriceInfo>
     */
    @PostMapping("materialPrice/selectPriceByInMaterialCodeAndDate")
    Map<String, CdMaterialPriceInfo> selectPriceByInMaterialCodeAndDate(@RequestParam(value = "materialCodes") String materialCodes,
                                                                        @RequestParam(value = "beginDate") String beginDate,
                                                                        @RequestParam(value = "endDate") String endDate);
    /**
     * 定时加工费价格同步
     * @return 成功或失败
     */
    @PostMapping("materialPrice/synPriceJGF")
    R synPriceJGF();

    /**
     * 定时原材料价格同步
     * @return 成功或失败
     */
    @PostMapping("materialPrice/synPriceYCL")
    R synPriceYCL();

    /**
     * 根据唯一索引查一条数据
     * @param materialCode
     * @param purchasingOrganization
     * @param memberCode
     * @return
     */
    @GetMapping("materialPrice/selectOneByCondition")
    R selectOneByCondition(@RequestParam("materialCode") String materialCode,
                           @RequestParam("purchasingOrganization") String purchasingOrganization ,
                           @RequestParam("memberCode") String memberCode);

}
