package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.order.converter.ProductionOrderDetailStatusConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class OmsProductionOrderDetailCommitExportVo{
    private static final long serialVersionUID = 1L;

    /**
     * 原材料号
     */
    @ExcelProperty(value = "原材料号",index = 0)
    @ApiModelProperty(value = "原材料号")
    private String materialCode;

    /**
     * 原材料描述
     */
    @ExcelProperty(value = "原材料描述",index = 1)
    @ApiModelProperty(value = "原材料描述")
    private String materialDesc;
    /**
     * 生产工厂
     */
    @ExcelProperty(value = "生产工厂",index = 2)
    @ApiModelProperty(value = "生产工厂")
    private String productFactoryCode;


    /**
     * 原材料排产量
     */
    @ExcelProperty(value = "原材料排产量",index = 3)
    @ApiModelProperty(value = "原材料排产量")
    private BigDecimal rawMaterialProductNum;
    /**
     * 基本开始日期
     */
    @ExcelProperty(value = "基本开始日期",index = 4)
    @ApiModelProperty(value = "基本开始日期")
    private String productStartDate;
    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 5)
    @ApiModelProperty(value = "单位")
    private String unit;


    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组",index = 6)
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 状态 0：未确认，1：已确认，2：反馈中
     */
    @ExcelProperty(value = "状态",index = 7,converter = ProductionOrderDetailStatusConverter.class )
    @ApiModelProperty(value = "状态 0：未确认，1：已确认，2：反馈中")
    private String status;

}
