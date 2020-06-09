package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 质量索赔 对象 sms_quality_order
 *
 * @author cs
 * @date 2020-05-28
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "质量索赔 ")
public class SmsQualityOrder extends BaseEntity {
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
    @ExcelProperty(value = "索赔单号")
    @ApiModelProperty(value = "索赔单号")
    private String qualityNo;

    /**
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 工厂
     */
    @ExcelProperty(value = "工厂")
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 专用号
     */
    @ExcelProperty(value = "专用号")
    @ApiModelProperty(value = "专用号")
    private String productMaterialCode;

    /**
     * 付款公司
     */
    @ExcelProperty(value = "付款公司")
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * 专用号名称
     */
    @ExcelProperty(value = "专用号名称")
    @ApiModelProperty(value = "专用号名称")
    private String productMaterialName;

    /**
     * 质量索赔状态 0待提交、1供应商待确认，2供应商确认，3超时自动确认、4 质量部待审核、5小微主待审核、6小微主审核通过， 7供应商待确认(申诉驳回)、 11待结算、12结算完成
     */
    @ExcelProperty(value = "质量索赔状态 ")
    @ApiModelProperty(value = "质量索赔状态 0待提交、1供应商待确认，2供应商确认，3超时自动确认、4 质量部待审核、5小微主待审核、6小微主审核通过， 7供应商待确认(申诉驳回)、 11待结算、12结算完成")
    private String qualityStatus;

    /**
     * 数量
     */
    @ExcelProperty(value = "数量")
    @ApiModelProperty(value = "数量")
    private Long qualityAmount;

    /**
     * 问题分类
     */
    @ExcelProperty(value = "问题分类")
    @ApiModelProperty(value = "问题分类")
    private String projectName;

    /**
     * 索赔条款
     */
    @ExcelProperty(value = "索赔条款")
    @ApiModelProperty(value = "索赔条款")
    private String claimClause;

    /**
     * 索赔理由
     */
    @ExcelProperty(value = "索赔理由")
    @ApiModelProperty(value = "索赔理由")
    private String claimReason;

    /**
     * 索赔金额
     */
    @ExcelProperty(value = "索赔金额")
    @ApiModelProperty(value = "索赔金额")
    private BigDecimal claimAmount;

    /**
     * 提交时间
     */
    @ExcelProperty(value = "提交时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date submitDate;

    /**
     * 申诉描述
     */
    @ExcelProperty(value = "申诉描述")
    @ApiModelProperty(value = "申诉描述")
    private String complaintDescription;

    /**
     * 申诉时间
     */
    @ExcelProperty(value = "申诉时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date complaintDate;

    /**
     * 供应商确认时间
     */
    @ExcelProperty(value = "供应商确认时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date supplierConfirmDate;

    /**
     * 结算单号
     */
    @ExcelProperty(value = "结算单号")
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 结算金额
     */
    @ExcelProperty(value = "结算金额")
    @ApiModelProperty(value = "结算金额")
    private BigDecimal settleFee;

    /**
     * 兑现金额
     */
    @ExcelProperty(value = "兑现金额")
    @ApiModelProperty(value = "兑现金额")
    private BigDecimal cashAmount;

    /**
     * 未兑现金额
     */
    @ExcelProperty(value = "未兑现金额")
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
    private String procDefId;

    /**
     * 流程实例名称
     */
    @Transient
    private String procName;

}
