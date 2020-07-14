package com.cloud.settle.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.DeplayStatusConverter;
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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 延期交付索赔 对象 sms_delays_delivery
 *
 * @author cs
 * @date 2020-06-04
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "延期交付索赔 ")
public class SmsDelaysDeliveryExportVo{
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 线体号
     */
    @ExcelProperty(value = "线体号",index = 4)
    @ApiModelProperty(value = "线体号")
    private String productLineCode;

    /**
     * 索赔单号
     */
    @ExcelProperty(value = "索赔单号",index = 3)
    @ApiModelProperty(value = "索赔单号")
    private String delaysNo;

    /**
     * 生产单号
     */
    @ExcelProperty(value = "生产订单号",index = 2)
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 工厂
     */
    @NotBlank(message = "工厂不能为空")
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 付款公司
     */
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * 专用号
     */
    @ExcelProperty(value = "专用号",index = 5)
    @ApiModelProperty(value = "专用号")
    private String productMaterialCode;

    /**
     * 专用号名称
     */
    @ExcelProperty(value = "专用号名称",index = 6)
    @ApiModelProperty(value = "专用号名称")
    private String productMaterialName;

    /**
     * 供应商编码
     */
    @NotBlank(message = "供应商编码不能为空")
    @ExcelProperty(value = "供应商编码",index = 0)
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称",index = 1)
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 索赔状态 1待供应商确认、4订单待审核、5小微主待审核、、6小微主审核通过、7 待供应商确认(申诉驳回) 、11待结算、12已结算、13已兑现、14部分兑现、15未兑现
     */
    @ExcelProperty(value = "索赔状态",index = 11,converter = DeplayStatusConverter.class)
    @ApiModelProperty(value = "索赔状态 1待供应商确认、4订单待审核、5小微主待审核、、6小微主审核通过、7 待供应商确认(申诉驳回) 、11待结算、12已结算、13已兑现、14部分兑现、15未兑现")
    private String delaysStatus;

    /**
     * 基本完成日期
     */
    @ExcelProperty(value = "基本完成日期",index = 9)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "基本完成日期")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date deliveryDate;

    /**
     * 实际完成日期
     */
    @ExcelProperty(value = "实际完成日期",index = 10)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "实际完成日期")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date actDeliveryDate;

    /**
     * 索赔金额
     */
    @ExcelProperty(value = "索赔金额(元)",index = 7)
    @ApiModelProperty(value = "索赔金额")
    private BigDecimal delaysAmount;

    /**
     * 提交时间
     */
    @ExcelProperty(value = "创建时间",index = 12)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date submitDate;

    /**
     * 申诉描述
     */
    @ExcelProperty(value = "申诉描述",index = 14)
    @ApiModelProperty(value = "申诉描述")
    private String complaintDescription;

    /**
     * 申诉时间
     */
    @ExcelProperty(value = "申诉时间",index = 15)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申诉时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date complaintDate;

    /**
     * 供应商确认时间
     */
    @ExcelProperty(value = "供应商确认时间",index = 13)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "供应商确认时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date supplierConfirmDate;

    /**
     * 结算单号
     */
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 结算金额
     */
    @ExcelProperty(value = "实际结算金额(元)",index = 8)
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
