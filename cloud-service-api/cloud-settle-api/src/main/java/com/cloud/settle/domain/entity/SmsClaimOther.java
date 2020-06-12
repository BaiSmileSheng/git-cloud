package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.ClaimOtherStatusConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import tk.mybatis.mapper.annotation.KeySql;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 其他索赔对象 sms_claim_other
 *
 * @author cs
 * @date 2020-06-02
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "其他索赔")
public class SmsClaimOther extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 索赔单号
     */
    @ExcelProperty(value = "索赔单号",index = 3)
    @ApiModelProperty(value = "索赔单号")
    private String claimCode;

    /**
     * 生产订单号
     */
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码",index = 0)
    @Valid
    @NotNull(message = "供应商编码不能为空")
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称",index = 1)
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 工厂
     */
    @ExcelProperty(value = "工厂",index = 2)
    @Valid
    @NotNull(message = "工厂不能为空")
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 付款公司
     */
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * 索赔原因
     */
    @ExcelProperty(value = "索赔原因",index = 4)
    @ApiModelProperty(value = "索赔原因")
    private String claimReson;

    /**
     * 索赔金额
     */
    @ExcelProperty(value = "索赔金额",index = 6)
    @ApiModelProperty(value = "索赔金额")
    private BigDecimal claimPrice;

    /**
     * 索赔状态 1供应商待确认、3小微主待审核、7 供应商待确认 11待结算、12 结算完成 、13已兑现、14部分兑现、15未兑现
     */
    @ExcelProperty(value = "索赔状态",index = 5,converter = ClaimOtherStatusConverter.class)
    @ApiModelProperty(value = "索赔状态 1供应商待确认、3小微主待审核、7 供应商待确认 11待结算、12 结算完成 、13已兑现、14部分兑现、15未兑现")
    private String claimOtherStatus;

    /**
     * 提交时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitDate;

    /**
     * 申诉描述
     */
    @ExcelProperty(value = "申诉描述",index = 8)
    @ApiModelProperty(value = "申诉描述")
    private String complaintDescription;

    /**
     * 申诉时间
     */
    @ExcelProperty(value = "申诉时间",index = 9)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申诉时间")
    private Date complaintDate;

    /**
     * 供应商确认时间
     */
    @ExcelProperty(value = "供应商确认时间",index = 7)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "供应商确认时间")
    private Date supplierConfirmDate;

    /**
     * 结算单号
     */
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 结算金额
     */
    @ApiModelProperty(value = "结算金额")
    private BigDecimal settleFee;

    /**
     * 兑现金额
     */
    @ApiModelProperty(value = "兑现金额")
    private BigDecimal cashAmount;

    /**
     * 未兑现金额
     */
    @ApiModelProperty(value = "未兑现金额")
    private BigDecimal uncashAmount;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 流程实例ID
     */
    @Transient
    @ApiModelProperty(value = "流程实例ID")
    private String procDefId;

    /**
     * 流程实例名称
     */
    @Transient
    @ApiModelProperty(value = "流程实例名称")
    private String procName;

    /**
     * 提交的标记(新增或修改时直接提交为true)
     */
    @Transient
    @ApiModelProperty(value = "提交的标记(新增或修改时直接提交为true)")
    private Boolean flagCommit;

}
