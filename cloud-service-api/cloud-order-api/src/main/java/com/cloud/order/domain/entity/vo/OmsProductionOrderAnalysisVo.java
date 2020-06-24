package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.order.domain.entity.OmsProductionOrderAnalysis;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * Description:  待排产订单分析Vo
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/18
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "待排产订单分析Vo ")
public class OmsProductionOrderAnalysisVo extends BaseEntity {

    private static final long serialVersionUID = 1L;
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
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码")
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;
    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;
    /**
     * 待排产订单分析数据List
     */
    @ExcelProperty(value = "待排产订单分析数据List")
    @ApiModelProperty(value = "待排产订单分析数据List")
    private List<OmsProductionOrderAnalysis> dataList;
}
