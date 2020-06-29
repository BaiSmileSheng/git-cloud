package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.QualityStatusConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @ExcelProperty(value = "索赔单号",index = 0)
    @ApiModelProperty(value = "索赔单号")
    private String qualityNo;

    /**
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号",index = 1)
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 供应商编码
     */
    @NotBlank(message = "供应商编码不能为空")
    @ExcelProperty(value = "供应商编码",index = 2)
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称",index = 3)
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 工厂
     */
    @NotBlank(message = "工厂不能为空")
    @ExcelProperty(value = "工厂",index = 4)
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 专用号
     */
    @ExcelProperty(value = "专用号",index = 5)
    @ApiModelProperty(value = "专用号")
    private String productMaterialCode;

    /**
     * 付款公司
     */
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * 专用号名称
     */
    @ExcelProperty(value = "专用号名称",index = 6)
    @ApiModelProperty(value = "专用号名称")
    private String productMaterialName;

    /**
     * 质量索赔状态 0待提交、1供应商待确认，2供应商确认，3超时自动确认、4 质量部待审核、5小微主待审核、6小微主审核通过， 7供应商待确认(申诉驳回)、 11待结算、12结算完成
     */
    @ExcelProperty(value = "质量索赔状态 ",index = 11,converter = QualityStatusConverter.class)
    @ApiModelProperty(value = "质量索赔状态 0待提交、1供应商待确认，2供应商确认，3超时自动确认、4 质量部待审核、5小微主待审核、6小微主审核通过， 7供应商待确认(申诉驳回)、 11待结算、12结算完成")
    private String qualityStatus;

    /**
     * 数量
     */
    @ExcelProperty(value = "数量",index = 7)
    @ApiModelProperty(value = "数量")
    private Long qualityAmount;

    /**
     * 问题分类
     */
    @NotBlank(message = "问题分类不能为空")
    @ExcelProperty(value = "问题分类",index = 9)
    @ApiModelProperty(value = "问题分类")
    private String projectName;

    /**
     * 问题分类id
     */
    @NotNull(message = "问题分类id不能为空")
    @ApiModelProperty(value = "问题分类id")
    private Long projectId;

    /**
     * 索赔条款
     */
    @NotBlank(message = "索赔条款不能为空")
    @ApiModelProperty(value = "索赔条款")
    private String claimClause;

    /**
     * 索赔条款id
     */
    @NotNull(message = "索赔条款id不能为空")
    @ApiModelProperty(value = "索赔条款id")
    private Long claimId;

    /**
     * 索赔理由
     */
    @ExcelProperty(value = "索赔理由",index = 10)
    @ApiModelProperty(value = "索赔理由")
    private String claimReason;

    /**
     * 索赔金额
     */
    @NotNull(message = "索赔金额不能为空")
    @ExcelProperty(value = "索赔金额",index = 8)
    @ApiModelProperty(value = "索赔金额")
    private BigDecimal claimAmount;

    /**
     * 提交时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date submitDate;

    /**
     * 申诉描述
     */
    @ExcelProperty(value = "申诉描述",index = 13)
    @ApiModelProperty(value = "申诉描述")
    private String complaintDescription;

    /**
     * 申诉时间
     */
    @ExcelProperty(value = "申诉时间",index = 14)
    @ApiModelProperty(value = "申诉时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date complaintDate;

    /**
     * 供应商确认时间
     */
    @ExcelProperty(value = "供应商确认时间",index = 12)
    @ApiModelProperty(value = "供应商确认时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
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
    private String procDefId;

    /**
     * 流程实例名称
     */
    @Transient
    private String procName;

}
