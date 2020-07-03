package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
/**
 * 原材料评审页面父级Vo
 *
 * @author ltq
 * @date 2020-06-28
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "原材料评审页面父级Vo ")
public class OmsProductionOrderDetailVo {
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
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;
    /**
     * 可用库存
     */
    @ExcelProperty(value = "可用库存")
    @ApiModelProperty(value = "可用库存")
    private BigDecimal stockNum;
    /**
     * 明细列表
     */
    @ExcelProperty(value = "明细列表")
    @ApiModelProperty(value = "明细列表")
    private List<RawMaterialReviewDetailVo> list;
}
