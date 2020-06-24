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

/**
 * 订单类测试对象 oms_test
 *
 * @author cloud
 * @date 2020-05-02
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "订单类测试")
public class OmsTest extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 测试字段一
     */
    @ExcelProperty(value = "测试字段一")
    @ApiModelProperty(value = "测试字段一")
    private String testA;

    /**
     * 测试字段二
     */
    @ExcelProperty(value = "测试字段二")
    @ApiModelProperty(value = "测试字段二")
    private Long testB;

}
