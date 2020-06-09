package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * 索赔兑现明细 对象 sms_claim_cash_detail
 *
 * @author cs
 * @date 2020-06-05
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "索赔兑现明细 ")
public class SmsClaimCashDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 索赔单号
     */
    @ExcelProperty(value = "索赔单号")
    @ApiModelProperty(value = "索赔单号")
    private String claimNo;

    /**
     * 索赔类型
     */
    @ExcelProperty(value = "索赔类型")
    @ApiModelProperty(value = "索赔类型")
    private String claimType;

    /**
     * 兑现金额
     */
    @ExcelProperty(value = "兑现金额")
    @ApiModelProperty(value = "兑现金额")
    private BigDecimal cashAmount;

    /**
     * 结算单号
     */
    @ExcelProperty(value = "结算单号")
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 应兑现月份
     */
    @ExcelProperty(value = "应兑现月份")
    @ApiModelProperty(value = "应兑现月份")
    private String shouldCashMounth;

    /**
     * 实际兑现月份
     */
    @ExcelProperty(value = "实际兑现月份")
    @ApiModelProperty(value = "实际兑现月份")
    private String actualCashMounth;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

}
