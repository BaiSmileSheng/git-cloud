package com.cloud.order.domain.entity;

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

import javax.persistence.Id;
import javax.persistence.Transient;

import tk.mybatis.mapper.annotation.KeySql;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 待排产订单分析 对象 oms_production_order_analysis
 *
 * @author ltq
 * @date 2020-06-15
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "待排产订单分析 ")
public class OmsProductionOrderAnalysis extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 订单来源 1：内单，2：外单
     */
    @ApiModelProperty(value = "订单来源 1：内单，2：外单")
    private String orderFrom;

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
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 需求数量
     */
    @ExcelProperty(value = "需求数量")
    @ApiModelProperty(value = "需求数量")
    private BigDecimal demandOrderNum;

    /**
     * 客户缺口量
     */
    @ExcelProperty(value = "客户缺口量")
    @ApiModelProperty(value = "客户缺口量")
    private BigDecimal customerBreachNum;

    /**
     * 缺口客户数
     */
    @ExcelProperty(value = "缺口客户数")
    @ApiModelProperty(value = "缺口客户数")
    private Long gapCustomer;

    /**
     * 总客户数
     */
    @ExcelProperty(value = "总客户数")
    @ApiModelProperty(value = "总客户数")
    private Long totalCustomer;

    /**
     * 结余量
     */
    @ExcelProperty(value = "结余量")
    @ApiModelProperty(value = "结余量")
    private BigDecimal surplusNum;

    /**
     * 可用总库存
     */
    @ExcelProperty(value = "可用总库存")
    @ApiModelProperty(value = "可用总库存")
    private BigDecimal stockNum;
    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 生产日期
     */
    @ExcelProperty(value = "生产日期")
    @ApiModelProperty(value = "生产日期")
    private String productDate;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;
    /**
     * 工厂权限
     */
    @Transient
    @ApiModelProperty(value = "生产工厂权限")
    private String productFactoryCodeList;

}
