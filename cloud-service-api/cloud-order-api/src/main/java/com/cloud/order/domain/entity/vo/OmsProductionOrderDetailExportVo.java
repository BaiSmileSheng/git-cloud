package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * 原材料导出Vo
 *
 * @author ltq
 * @date 2020-06-28
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "原材料导出Vo ")
public class OmsProductionOrderDetailExportVo {
    private static final long serialVersionUID = 1L;
    /**
     * 原材料号
     */
    @ExcelProperty(value = "原材料号",index = 0)
    private String materialCode;

    /**
     * 原材料描述
     */
    @ExcelProperty(value = "原材料描述",index = 1)
    private String materialDesc;

    /**
     * 生产工厂
     */
    @ExcelProperty(value = "生产工厂",index = 2)
    private String productFactoryCode;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 3)
    private String unit;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组",index = 4)
    private String purchaseGroup;
    /**
     * 可用库存
     */
    @ExcelProperty(value = "可用库存",index = 5)
    private BigDecimal stockNum;
    /**
     * T+1天
     */
    @ExcelProperty(value = "T+1天")
    private BigDecimal day1;
    /**
     * T+1天缺口量
     */
    @ExcelProperty(value = "T+1天缺口量")
    private BigDecimal day1Gap;
    /**
     * T+2天
     */
    @ExcelProperty(value = "T+2天")
    private BigDecimal day2;
    /**
     * T+2天缺口量
     */
    @ExcelProperty(value = "T+2天缺口量")
    private BigDecimal day2Gap;
    /**
     * T+3天
     */
    @ExcelProperty(value = "T+3天")
    private BigDecimal day3;
    /**
     * T+3天缺口量
     */
    @ExcelProperty(value = "T+3天缺口量")
    private BigDecimal day3Gap;
    /**
     * T+4天
     */
    @ExcelProperty(value = "T+4天")
    private BigDecimal day4;
    /**
     * T+4天缺口量
     */
    @ExcelProperty(value = "T+4天缺口量")
    private BigDecimal day4Gap;
    /**
     * T+5天
     */
    @ExcelProperty(value = "T+5天")
    private BigDecimal day5;
    /**
     * T+5天缺口量
     */
    @ExcelProperty(value = "T+5天缺口量")
    private BigDecimal day5Gap;
    /**
     * T+6天
     */
    @ExcelProperty(value = "T+6天")
    private BigDecimal day6;
    /**
     * T+6天缺口量
     */
    @ExcelProperty(value = "T+6天缺口量")
    private BigDecimal day6Gap;
    /**
     * T+7天
     */
    @ExcelProperty(value = "T+7天")
    private BigDecimal day7;
    /**
     * T+7天缺口量
     */
    @ExcelProperty(value = "T+7天缺口量")
    private BigDecimal day7Gap;
    /**
     * T+8天
     */
    @ExcelProperty(value = "T+8天")
    private BigDecimal day8;
    /**
     * T+8天缺口量
     */
    @ExcelProperty(value = "T+8天缺口量")
    private BigDecimal day8Gap;
    /**
     * T+9天
     */
    @ExcelProperty(value = "T+9天")
    private BigDecimal day9;
    /**
     * T+9天缺口量
     */
    @ExcelProperty(value = "T+9天缺口量")
    private BigDecimal day9Gap;
    /**
     * T+10天
     */
    @ExcelProperty(value = "T+10天")
    private BigDecimal day10;
    /**
     * T+10天缺口量
     */
    @ExcelProperty(value = "T+10天缺口量")
    private BigDecimal day10Gap;
    /**
     * T+11天
     */
    @ExcelProperty(value = "T+11天")
    private BigDecimal day11;
    /**
     * T+11天缺口量
     */
    @ExcelProperty(value = "T+11天缺口量")
    private BigDecimal day11Gap;
    /**
     * T+12天
     */
    @ExcelProperty(value = "T+12天")
    private BigDecimal day12;
    /**
     * T+12天缺口量
     */
    @ExcelProperty(value = "T+12天缺口量")
    private BigDecimal day12Gap;
    /**
     * T+13天
     */
    @ExcelProperty(value = "T+13天")
    private BigDecimal day13;
    /**
     * T+13天缺口量
     */
    @ExcelProperty(value = "T+13天缺口量")
    private BigDecimal day13Gap;
    /**
     * T+14天
     */
    @ExcelProperty(value = "T+14天")
    private BigDecimal day14;
    /**
     * T+14天缺口量
     */
    @ExcelProperty(value = "T+14天缺口量")
    private BigDecimal day14Gap;

}
