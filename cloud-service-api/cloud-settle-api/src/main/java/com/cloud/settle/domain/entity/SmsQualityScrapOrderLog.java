package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

import tk.mybatis.mapper.annotation.KeySql;

import java.util.Date;

/**
 * 质量部报废申诉对象 sms_quality_scrap_order_log
 *
 * @author ltq
 * @date 2020-12-18
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "质量部报废申诉")
public class SmsQualityScrapOrderLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 质量部报废单号
     */
    @ExcelProperty(value = "质量部报废单号")
    @ApiModelProperty(value = "质量部报废单号")
    private String qualityNo;

    /**
     * 1、质量经理 2、质量部部长  3、质量平台长
     */
    @ExcelProperty(value = "1、质量经理 2、质量部部长  3、质量平台长")
    @ApiModelProperty(value = "1、质量经理 2、质量部部长  3、质量平台长")
    private String procNo;

    /**
     * 申诉描述
     */
    @ExcelProperty(value = "申诉描述")
    @ApiModelProperty(value = "申诉描述")
    private String complaintDescription;

    /**
     * 申诉时间
     */
    @ExcelProperty(value = "申诉时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申诉时间")
    private Date complaintDate;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 质量部报废表ID
     */
    @ExcelProperty(value = "质量部报废表ID")
    @ApiModelProperty(value = "质量部报废表ID")
    private Long qualityId;

    /**
     * 审核结果 2通过 3驳回
     */
    @ExcelProperty(value = "审核结果 2通过 3驳回")
    @ApiModelProperty(value = "审核结果 2通过 3驳回")
    private Integer result;

    /**
     * 审核意见
     */
    @ExcelProperty(value = "审核意见")
    @ApiModelProperty(value = "审核意见")
    private String comment;

    /**
     * 审批人
     */
    @ExcelProperty(value = "审批人")
    @ApiModelProperty(value = "审批人")
    private String auditor;

    /**
     * 审批时间
     */
    @ExcelProperty(value = "审批时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审批时间")
    private Date auditTime;

}
