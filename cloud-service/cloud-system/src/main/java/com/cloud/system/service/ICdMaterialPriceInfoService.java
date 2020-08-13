package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.CdSettleProductMaterial;

import java.util.List;
import java.util.Map;

/**
 * SAP成本价格 Service接口
 *
 * @author cs
 * @date 2020-05-26
 */
public interface ICdMaterialPriceInfoService extends BaseService<CdMaterialPriceInfo> {

    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     * @param materialCode
     * @return
     */
    R checkSynchroSAP(String materialCode);

    /**
     * 根据物料号和采购组织分组查询
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode+organization,CdMaterialPriceInfo>
     */
    Map<String, CdMaterialPriceInfo> selectPriceByInMaterialCodeAndDate(List<String> materialCodes,
                                                                        String beginDate,
                                                                        String endDate);
    /**
     * 定时加工费价格同步
     * @return 成功或失败
     */
    R synPriceJGF();

    /**
     * 定时原材料价格同步
     * @return 成功或失败
     */
    R synPriceYCL();
    /**
     * Description:  根据成品物料号查询SAP成本价格
     * Param: [materialCodes]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/8/3
     */
    R selectMaterialPrice(Map<String,List<CdSettleProductMaterial>> map);
}
