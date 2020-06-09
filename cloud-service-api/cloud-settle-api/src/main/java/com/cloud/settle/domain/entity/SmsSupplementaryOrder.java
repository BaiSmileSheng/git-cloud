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
 * 物耗申请单 对象 sms_supplementary_order
 *
 * @author cs
 * @date 2020-05-26
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "物耗申请单 ")
public class SmsSupplementaryOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 物耗单号
     */
    @ExcelProperty(value = "物耗单号")
    @ApiModelProperty(value = "物耗单号")
    private String stuffNo;

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
     * 付款公司
     */
    @ExcelProperty(value = "付款公司")
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成
     */
    @ExcelProperty(value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成")
    @ApiModelProperty(value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成")
    private String stuffStatus;

    /**
     * 原材料物料号
     */
    @ExcelProperty(value = "原材料物料号")
    @ApiModelProperty(value = "原材料物料号")
    private String rawMaterialCode;

    /**
     * 原材料物料名称
     */
    @ExcelProperty(value = "原材料物料名称")
    @ApiModelProperty(value = "原材料物料名称")
    private String rawMaterialName;

    /**
     * 申请数量
     */
    @ExcelProperty(value = "申请数量")
    @ApiModelProperty(value = "申请数量")
    private Integer stuffAmount;

    /**
     * 物料单价
     */
    @ExcelProperty(value = "物料单价")
    @ApiModelProperty(value = "物料单价")
    private BigDecimal stuffPrice;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String stuffUnit;

    /**
     * 币种
     */
    @ExcelProperty(value = "币种")
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 汇率
     */
    @ExcelProperty(value = "汇率")
    @ApiModelProperty(value = "汇率")
    private BigDecimal rate;

    /**
     * 提交时间
     */
    @ExcelProperty(value = "提交时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date submitDate;

    /**
     * 库存地点
     */
    @ExcelProperty(value = "库存地点")
    @ApiModelProperty(value = "库存地点")
    private String sapStoreage;

    /**
     * SAP创单结果
     */
    @ExcelProperty(value = "SAP创单结果")
    @ApiModelProperty(value = "SAP创单结果")
    private String sapResult;

    /**
     * SAP创单备注
     */
    @ExcelProperty(value = "SAP创单备注")
    @ApiModelProperty(value = "SAP创单备注")
    private String sapRemark;

    /**
     * SAP创单凭证
     */
    @ExcelProperty(value = "SAP创单凭证")
    @ApiModelProperty(value = "SAP创单凭证")
    private String postingNo;

    /**
     * SAP创单时间
     */
    @ExcelProperty(value = "SAP创单时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date sapDate;

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

    /** 兑现金额 */
    @ExcelProperty(value = "兑现金额")
    @ApiModelProperty(value = "兑现金额")
    private BigDecimal cashAmount;

    /** 兑现金额 */
    @ExcelProperty(value = "兑现金额")
    @ApiModelProperty(value = "兑现金额")
    private BigDecimal uncashAmount;

    /** 采购组 */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroupCode;

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
