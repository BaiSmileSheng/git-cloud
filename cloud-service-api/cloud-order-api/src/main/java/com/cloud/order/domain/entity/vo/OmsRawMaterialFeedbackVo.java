package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "反馈信息VO")
public class OmsRawMaterialFeedbackVo {
    private static final long serialVersionUID = 1L;
    /**
     * 成品物料号
     */
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 线体号
     */
    @ApiModelProperty(value = "线体号")
    private String productLineCode;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 基本开始日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "基本开始日期")
    private String productStartDate;

    /**
     * 基本结束日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "基本结束日期")
    private String productEndDate;

    /**
     * 基本结束时间
     */
    @ApiModelProperty(value = "基本结束时间")
    private String productEndTime;
    /**
     * 原材料物料号
     */
    @ApiModelProperty(value = "原材料物料号")
    private String rawMaterialCode;

    /**
     * 原材料物料描述
     */
    @ApiModelProperty(value = "原材料物料描述")
    private String rawMaterialDesc;

    /**
     * 交付日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "交付日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date deliveryDate;

    /**
     * 成品排产量
     */
    @ApiModelProperty(value = "成品排产量")
    private BigDecimal productNum;

    /**
     * 原材料排产量
     */
    @ApiModelProperty(value = "原材料排产量")
    private BigDecimal rawMaterialNum;

    /**
     * 成品满足量
     */
    @ApiModelProperty(value = "成品满足量")
    private BigDecimal productContentNum;

    /**
     * 原材料满足量
     */
    @ApiModelProperty(value = "原材料满足量")
    private BigDecimal rawMaterialContentNum;

    /**
     * 状态 0：未审核，1：通过，2：驳回，3：未审核已下达
     */
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
     * 生产工厂权限查询
     */
    @Transient
    @ApiModelProperty(value = "生产工厂权限查询")
    private String checkProductFactory;
    /**
     * 反馈人
     */
    @Transient
    @ApiModelProperty(value = "反馈人")
    private String createBy;

    /**
     * 备注
     */
    @Transient
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 创建时间
     */
    @Transient
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
