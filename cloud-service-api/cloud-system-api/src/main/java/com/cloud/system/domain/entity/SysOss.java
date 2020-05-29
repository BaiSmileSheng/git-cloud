package com.cloud.system.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "文件上传")
@Table(name = "sys_oss")
public class SysOss implements Serializable {
    //
    private static final long serialVersionUID = 1356257283938225230L;

    @Id
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件后缀
     */
    @ApiModelProperty(value = "文件后缀")
    private String fileSuffix;

    /**
     * URL地址
     */
    @ApiModelProperty(value = "URL地址")
    private String url;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 上传者
     */
    @ApiModelProperty(value = "上传者")
    private String createBy;

    /**
     * 服务商
     */
    @ApiModelProperty(value = "服务商")
    private Integer service;

    /**
     * 用于表格行内编辑
     */
    @ApiModelProperty(value = "用于表格行内编辑")
    @Transient
    private Boolean editable;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNo;

}
