package com.cloud.settle.domain.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 索赔兑现明细 对象
 *
 * @author cs
 * @date 2020-06-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsClaimCashDetailVo {
    private static final long serialVersionUID = 1L;

    /**
     * 索赔类型
     */
    @ApiModelProperty(value = "索赔类型")
    private String claimType;

    /**
     * 实际兑现金额
     */
    @ApiModelProperty(value = "实际兑现金额")
    private BigDecimal actualCashAmount;

    /**
     * 历史兑现金额
     */
    @ApiModelProperty(value = "历史兑现金额")
    private BigDecimal historyCashAmount;

}
