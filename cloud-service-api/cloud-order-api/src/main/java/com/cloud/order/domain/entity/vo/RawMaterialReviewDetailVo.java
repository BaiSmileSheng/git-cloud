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
/**
 * 原材料评审页面Vo
 *
 * @author ltq
 * @date 2020-06-28
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "原材料评审页面Vo ")
public class RawMaterialReviewDetailVo {
    /**
     * 开始日期
     */
    @ExcelProperty(value = "开始日期")
    @ApiModelProperty(value = "开始日期")
    private String productStartDate;
    /**
     * 排产量
     */
    @ExcelProperty(value = "排产量")
    @ApiModelProperty(value = "排产量")
    private BigDecimal productNum;
    /**
     * 缺口量
     */
    @ExcelProperty(value = "缺口量")
    @ApiModelProperty(value = "缺口量")
    private BigDecimal gapNum;
}
