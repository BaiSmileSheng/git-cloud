package com.cloud.settle.domain.entity;

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
import java.util.List;

/**
 * 发票信息 对象 sms_invoice_info
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "发票信息 ")
public class SmsInvoiceInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 月度结算单号
     */
    @ExcelProperty(value = "月度结算单号")
    @ApiModelProperty(value = "月度结算单号")
    private String mouthSettleId;

    /**
     * 发票号
     */
    @ExcelProperty(value = "发票号")
    @ApiModelProperty(value = "发票号")
    private String invoiceNo;

    /**
     * 发票时间
     */
    @ExcelProperty(value = "发票时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发票时间")
    private Date invoiceDate;

    /**
     * 发票金额
     */
    @ExcelProperty(value = "发票金额")
    @ApiModelProperty(value = "发票金额")
    private BigDecimal invoiceAmount;

    /**
     * 税率
     */
    @ExcelProperty(value = "税率")
    @ApiModelProperty(value = "税率")
    private BigDecimal invoiceRate;

    /**
     * 税额
     */
    @ExcelProperty(value = "税额")
    @ApiModelProperty(value = "税额")
    private BigDecimal taxAmount;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

}
