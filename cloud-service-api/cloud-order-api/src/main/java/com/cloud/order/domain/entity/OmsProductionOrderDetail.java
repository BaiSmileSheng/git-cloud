package com.cloud.order.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

import tk.mybatis.mapper.annotation.KeySql;

import java.math.BigDecimal;

/**
 * 排产订单明细 对象 oms_production_order_detail
 *
 * @author ltq
 * @date 2020-06-19
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "排产订单明细 ")
public class OmsProductionOrderDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    @ApiModelProperty(value = "主键")
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 排产订单号
     */
    @ExcelProperty(value = "排产订单号")
    @ApiModelProperty(value = "排产订单号")
    private String productOrderCode;
    /**
     * 生产工厂
     */
    @ExcelProperty(value = "生产工厂")
    @ApiModelProperty(value = "生产工厂")
    private String productFactoryCode;

    /**
     * 原材料号
     */
    @ExcelProperty(value = "原材料号")
    @ApiModelProperty(value = "原材料号")
    private String materialCode;

    /**
     * 原材料描述
     */
    @ExcelProperty(value = "原材料描述")
    @ApiModelProperty(value = "原材料描述")
    private String materialDesc;

    /**
     * 单耗
     */
    @ExcelProperty(value = "单耗")
    @ApiModelProperty(value = "单耗")
    private BigDecimal bomNum;

    /**
     * 基本数量
     */
    @ExcelProperty(value = "基本数量")
    @ApiModelProperty(value = "基本数量")
    private BigDecimal basicNum;

    /**
     * 原材料排产量
     */
    @ExcelProperty(value = "原材料排产量")
    @ApiModelProperty(value = "原材料排产量")
    private BigDecimal rawMaterialProductNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "BOM版本")
    @ApiModelProperty(value = "BOM版本")
    private String bomVersion;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 仓储点
     */
    @ExcelProperty(value = "仓储点")
    @ApiModelProperty(value = "仓储点")
    private String storagePoint;

    /**
     * 状态 0：未确认，1：已确认，2：反馈中
     */
    @ExcelProperty(value = "状态 0：未确认，1：已确认，2：反馈中")
    @ApiModelProperty(value = "状态 0：未确认，1：已确认，2：反馈中")
    private String status;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

}
