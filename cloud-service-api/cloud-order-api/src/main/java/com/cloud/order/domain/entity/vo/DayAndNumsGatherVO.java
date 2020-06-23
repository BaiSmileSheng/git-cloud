package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * T+1、T+2草稿计划对比分析展示对象
 *
 * @author cs
 * @date 2020-06-22
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "T+1、T+2草稿计划对比分析展示对象")
public class DayAndNumsGatherVO {
    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private String day;

    /**
     * 接口接入量
     */
    @ApiModelProperty(value = "接口接入量")
    private Long interfaceNum;

    /**
     * 人工导入量
     */
    @ApiModelProperty(value = "人工导入量")
    private Long artificialNum;

    /**
     * 差异量
     */
    @ApiModelProperty(value = "差异量")
    private Long differenceNum;
}
