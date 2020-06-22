package com.cloud.order.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.cloud.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 汇总展示对象
 *
 * @author cs
 * @date 2020-06-18
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "汇总展示对象 ")
public class WeekAndNumGatherDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 周数 交货日期周数
     */
    @ApiModelProperty(value = "周数 交货日期周数")
    private String weeks;

    /**
     * 订单数量
     */
    @ApiModelProperty(value = "订单数量")
    private Long orderNum;

    /**
     * 周开始日期
     */
    private String startDate;
    /**
     * 周结束日期
     */
    private String endDate;

}
