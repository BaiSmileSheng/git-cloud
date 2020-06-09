package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.SettleStatusConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 月度结算信息 对象 sms_mouth_settle
 *
 * @author cs
 * @date 2020-06-04
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "月度结算信息 ")
public class SmsMouthSettle extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 结算单号
     */
    @ExcelProperty(value = "结算单号",index = 0)
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 结算月份
     */
    @ApiModelProperty(value = "结算月份")
    private String dataMoth;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码",index = 1)
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称",index = 2)
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 付款公司
     */
    @ExcelProperty(value = "付款公司",index = 3)
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * 结算加工费
     */
    @ExcelProperty(value = "加工费(元)",index = 4)
    @ApiModelProperty(value = "结算加工费")
    private BigDecimal machiningAmount;

    /**
     * 本月应扣款汇总
     */
    @ExcelProperty(value = "本月应扣款汇总(元)",index = 5)
    @ApiModelProperty(value = "本月应扣款汇总")
    private BigDecimal claimAmount;

    /**
     * 历史未兑现金额
     */
    @ExcelProperty(value = "历史未兑现金额(元)",index = 6)
    @ApiModelProperty(value = "历史未兑现金额")
    private BigDecimal noCashAmount;

    /**
     * 本月兑现金额
     */
    @ExcelProperty(value = "本月兑现金额(元)",index = 7)
    @ApiModelProperty(value = "本月兑现金额")
    private BigDecimal cashAmount;

    /**
     * 不含税金额
     */
    @ApiModelProperty(value = "不含税金额")
    private BigDecimal excludingFee;

    /**
     * 含税金额
     */
    @ExcelProperty(value = "含税金额",index = 8)
    @ApiModelProperty(value = "含税金额")
    private BigDecimal includeTaxeFee;

    /**
     * 发票金额
     */
    @ExcelProperty(value = "发票金额",index = 9)
    @ApiModelProperty(value = "发票金额")
    private BigDecimal invoiceFee;

    /**
     * KEMS单据号
     */
    @ExcelProperty(value = "KEMS单据号",index = 10)
    @ApiModelProperty(value = "KEMS单据号")
    private String kmsNo;

    /**
     * 财务付款状态
     */
    @ApiModelProperty(value = "财务付款状态")
    private String kmsStatus;

    /**
     * 财务付款时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "财务付款时间")
    private Date kmsPayDate;

    /**
     * 结算状态 11待结算、13内控确认、14小微主确认、15待付款、12结算完成
     */
    @ExcelProperty(value = "结算状态",index = 11,converter = SettleStatusConverter.class)
    @ApiModelProperty(value = "结算状态 11待结算、13内控确认、14小微主确认、15待付款、12结算完成")
    private String settleStatus;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

}
