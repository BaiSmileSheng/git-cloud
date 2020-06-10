package com.cloud.settle.domain.entity.PO;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
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
@ApiModel(value = "发票信息集合")
public class SmsInvoiceInfoS extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 月度结算单号
     */
    @ExcelProperty(value = "月度结算单号")
    @ApiModelProperty(value = "月度结算单号")
    private String mouthSettleId;

    /**
     * 发票信息集合
     */
    @Transient
    @ApiModelProperty(value = "发票信息集合")
    private List<SmsInvoiceInfo> smsInvoiceInfoList;

}
