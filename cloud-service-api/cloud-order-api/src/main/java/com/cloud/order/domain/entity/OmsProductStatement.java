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
 * T-1交付考核报 对象 oms_product_statement
 *
 * @author lihongxia
 * @date 2020-08-10
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "T-1交付考核报 ")
public class OmsProductStatement extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

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
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码")
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ExcelProperty(value = "生产工厂描述")
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 应交货日期
     */
    @ExcelProperty(value = "应交货日期")
    @ApiModelProperty(value = "应交货日期")
    private String deliveryDate;

    /**
     * 应交付量
     */
    @ExcelProperty(value = "应交付量")
    @ApiModelProperty(value = "应交付量")
    private BigDecimal deliveryNum;

    /**
     * 成品库存
     */
    @ExcelProperty(value = "成品库存")
    @ApiModelProperty(value = "成品库存")
    private BigDecimal sumNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 拖期天数
     */
    @ExcelProperty(value = "拖期天数")
    @ApiModelProperty(value = "拖期天数")
    private Long delaysDays;

    /**
     * 状态:0未关闭,1已关闭
     */
    @ExcelProperty(value = "状态:0未关闭,1已关闭")
    @ApiModelProperty(value = "状态:0未关闭,1已关闭")
    private String status;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
