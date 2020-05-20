package com.cloud.system.domain.entity;

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
 * 接口调用日志 sys_interface_log
 *
 * @author cs
 * @date 2020-05-20
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "接口调用日志")
public class SysInterfaceLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    private Long id;

    /**
     * 调用方系统缩写，如：SAP，HDY……
     */
    @ExcelProperty(value = "调用方系统缩写，如：SAP，HDY……")
    @ApiModelProperty(value = "调用方系统缩写，如：SAP，HDY……")
    private String appId;

    /**
     * 接口对应的业务订单单号
     */
    @ExcelProperty(value = "接口对应的业务订单单号")
    @ApiModelProperty(value = "接口对应的业务订单单号")
    private String orderCode;

    /**
     * 接口名
     */
    @ExcelProperty(value = "接口名")
    @ApiModelProperty(value = "接口名")
    private String interfaceName;

    /**
     * 传输报文
     */
    @ExcelProperty(value = "传输报文")
    @ApiModelProperty(value = "传输报文")
    private String content;

    /**
     * 结果报文
     */
    @ExcelProperty(value = "结果报文")
    @ApiModelProperty(value = "结果报文")
    private String results;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ExcelProperty(value = "删除标志（0代表存在 2代表删除）")
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;



}
