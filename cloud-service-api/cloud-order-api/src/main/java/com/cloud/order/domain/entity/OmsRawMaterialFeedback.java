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
 * 原材料反馈信息 对象 oms_raw_material_feedback
 *
 * @author ltq
 * @date 2020-06-22
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "原材料反馈信息 ")
public class OmsRawMaterialFeedback extends BaseEntity {
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
     * 生产工厂
     */
    @ExcelProperty(value = "生产工厂")
    @ApiModelProperty(value = "生产工厂")
    private String productFactoryCode;
    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品物料描述")
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 原材料物料号
     */
    @ExcelProperty(value = "原材料物料号")
    @ApiModelProperty(value = "原材料物料号")
    private String rawMaterialCode;

    /**
     * 原材料物料描述
     */
    @ExcelProperty(value = "原材料物料描述")
    @ApiModelProperty(value = "原材料物料描述")
    private String rawMaterialDesc;

    /**
     * 基本开始日期
     */
    @ExcelProperty(value = "基本开始日期")
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "基本开始日期")
    private String productStartDate;

    /**
     * 成品排产量
     */
    @ExcelProperty(value = "成品排产量")
    @ApiModelProperty(value = "成品排产量")
    private BigDecimal productNum;

    /**
     * 原材料排产量
     */
    @ExcelProperty(value = "原材料排产量")
    @ApiModelProperty(value = "原材料排产量")
    private BigDecimal rawMaterialNum;

    /**
     * 成品满足量
     */
    @ExcelProperty(value = "成品满足量")
    @ApiModelProperty(value = "成品满足量")
    private BigDecimal productContentNum;

    /**
     * 原材料满足量
     */
    @ExcelProperty(value = "原材料满足量")
    @ApiModelProperty(value = "原材料满足量")
    private BigDecimal rawMaterialContentNum;
    /**
     * bom版本
     */
    @ExcelProperty(value = "版本")
    @ApiModelProperty(value = "bom版本")
    private String bomVersion;

    /**
     * 状态 0：未审核，1：通过，2：驳回，3：未审核已下达
     */
    @ExcelProperty(value = "状态 0：未审核，1：通过，2：驳回，3：未审核已下达")
    @ApiModelProperty(value = "状态 0：未审核，1：通过，2：驳回，3：未审核已下达")
    private String status;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 查询日期起始值
     */
    @Transient
    @ApiModelProperty(value = "查询日期起始值")
    private String checkDateStart;

    /**
     * 查询日期结束值
     */
    @Transient
    @ApiModelProperty(value = "查询日期结束值")
    private String checkDateEnd;
    /**
     * 通过/驳回标识
     */
    @Transient
    @ApiModelProperty(value = "通过/驳回标识")
    private String approvalFlag;
    /**
     * id字符串，逗号隔开
     */
    @Transient
    @ApiModelProperty(value = "id字符串，逗号隔开")
    private String ids;

}
