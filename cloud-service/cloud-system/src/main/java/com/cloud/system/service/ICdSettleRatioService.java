package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdSettleRatio;
import com.cloud.common.core.service.BaseService;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 结算索赔系数 Service接口
 *
 * @author cs
 * @date 2020-06-04
 */
public interface ICdSettleRatioService extends BaseService<CdSettleRatio> {

    /**
     * 新增结算索赔系数(先校验此索赔类型是否存在)
     * @param cdSettleRatio 结算索赔系数信息
     * @return 新增主键id
     */
    R addSaveVerifyClaimType(CdSettleRatio cdSettleRatio);

    /**
     * 修改保存结算索赔系数
     * @param cdSettleRatio 结算索赔系数信息
     * @return 修改结果成功或失败
     */
    R updateVerifyClaimType(CdSettleRatio cdSettleRatio);

    /**
     * 根据索赔类型查结算索赔系数
     * @param claimType 索赔类型
     * @return 结算索赔系数信息
     */
    CdSettleRatio selectByClaimType(String claimType);
}
