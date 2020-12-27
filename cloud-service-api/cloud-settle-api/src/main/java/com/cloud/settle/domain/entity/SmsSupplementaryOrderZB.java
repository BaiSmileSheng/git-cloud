package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.SupplementaryOrderStatusConverter;
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
public class SmsSupplementaryOrderZB extends BaseEntity {
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
    @ExcelProperty(value = "物耗单号",index = 1)
    @ApiModelProperty(value = "物耗单号")
    private String stuffNo;

    /**
     * 供应商编码
     */
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
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号",index = 0)
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成
     */
    @ExcelProperty(value = "物耗单状态",index = 13,converter = SupplementaryOrderStatusConverter.class)
    @ApiModelProperty(value = "订单状态 0 待提交、1jit待审核、2jit驳回、3小微主待审核、4小微主审核通过、5小微主驳回、 6 SAP成功、7 SAP创单失败、 11待结算、 12结算完成")
    private String stuffStatus;

    /**
     * 原材料物料号
     */
    @ExcelProperty(value = "物料号",index = 4)
    @ApiModelProperty(value = "原材料物料号")
    private String rawMaterialCode;

    /**
     * 原材料物料名称
     */
    @ExcelProperty(value = "物料名称",index = 5)
    @ApiModelProperty(value = "原材料物料名称")
    private String rawMaterialName;

    /**
     * 申请数量
     */
    @ExcelProperty(value = "物耗数量",index = 6)
    @ApiModelProperty(value = "申请数量")
    private Integer stuffAmount;

    /**
     * 物料单价
     */
    @ExcelProperty(value = "物料单价",index = 8)
    @ApiModelProperty(value = "物料单价")
    private BigDecimal stuffPrice;

    /**
     * 单位
     */
    @ExcelProperty(value = "计量单位",index = 7)
    @ApiModelProperty(value = "单位")
    private String stuffUnit;

    /**
     * 币种
     */
    @ExcelProperty(value = "币种",index = 9)
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 汇率
     */
    @ExcelProperty(value = "汇率",index = 10)
    @ApiModelProperty(value = "汇率")
    private BigDecimal rate;


    /**
     * SAP创单备注
     */
    @ExcelProperty(value = "SAP创单备注",index = 12)
    @ApiModelProperty(value = "SAP创单备注")
    private String sapRemark;

    /**
     * SAP创单凭证
     */
    @ExcelProperty(value = "SAP创单凭证",index = 11)
    @ApiModelProperty(value = "SAP创单凭证")
    private String postingNo;

    @ExcelProperty(value = "结算金额",index = 14)
    @ApiModelProperty(value = "结算金额")
    private BigDecimal settleFee;


}
