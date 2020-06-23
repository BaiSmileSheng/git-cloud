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
 * 内单PR/PO原 对象 oms_internal_order_res
 *
 * @author ltq
 * @date 2020-06-05
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "内单PR/PO原 ")
public class OmsInternalOrderRes extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * PR/PO号
     */
    @ExcelProperty(value = "PR/PO号")
    @ApiModelProperty(value = "PR/PO号")
    private String orderCode;

    /**
     * PR/PO行号
     */
    @ExcelProperty(value = "PR/PO行号")
    @ApiModelProperty(value = "PR/PO行号")
    private String orderLineCode;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料号")
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品物料描述")
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂
     */
    @ExcelProperty(value = "生产工厂")
    @ApiModelProperty(value = "生产工厂")
    private String productFactoryCode;

    /**
     * 生产工厂名称
     */
    @ExcelProperty(value = "生产工厂名称")
    @ApiModelProperty(value = "生产工厂名称")
    private String productFactoryDesc;

    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户描述")
    @ApiModelProperty(value = "客户描述")
    private String customerDesc;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroupCode;

    /**
     * 采购组描述
     */
    @ExcelProperty(value = "采购组描述")
    @ApiModelProperty(value = "采购组描述")
    private String purchaseGroupDesc;

    /**
     * 供应商代码
     */
    @ExcelProperty(value = "供应商代码")
    @ApiModelProperty(value = "供应商代码")
    private String supplierCode;

    /**
     * 供应商描述
     */
    @ExcelProperty(value = "供应商描述")
    @ApiModelProperty(value = "供应商描述")
    private String supplierDesc;

    /**
     * 交付日期
     */
    @ExcelProperty(value = "交付日期")
    @ApiModelProperty(value = "交付日期")
    private String deliveryDate;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "订单数量")
    @ApiModelProperty(value = "订单数量")
    private BigDecimal orderNum;

    /**
     * 已交货数量
     */
    @ExcelProperty(value = "已交货数量")
    @ApiModelProperty(value = "已交货数量")
    private BigDecimal deliveryNum;

    /**
     * 已删除标识：L：已删除
     */
    @ExcelProperty(value = "已删除标识：L：已删除")
    @ApiModelProperty(value = "已删除标识：L：已删除")
    private String sapDelFlag;

    /**
     * 交货完成标识：X：已交货完成
     */
    @ExcelProperty(value = "交货完成标识：X：已交货完成")
    @ApiModelProperty(value = "交货完成标识：X：已交货完成")
    private String deliveryFlag;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    @ApiModelProperty(value = "状态")
    private String status;

    /**
     * PR创建日期
     */
    @ExcelProperty(value = "PR创建日期")
    @ApiModelProperty(value = "PR创建日期")
    private String createDatePr;

    /**
     * 项目类别
     */
    @ExcelProperty(value = "项目类别")
    @ApiModelProperty(value = "项目类别")
    private String projectType;

    /**
     * MRP范围
     */
    @ExcelProperty(value = "MRP范围")
    @ApiModelProperty(value = "MRP范围")
    private String mrpRange;

    /**
     * 标记位 PR/PO
     */
    @ExcelProperty(value = "标记位 PR/PO")
    @ApiModelProperty(value = "标记位 PR/PO")
    private String marker;

    /**
     * 版本
     */
    @ExcelProperty(value = "版本")
    @ApiModelProperty(value = "版本")
    private String version;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

}
