package com.cloud.settle.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.QualityScrapOrderStatusConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

/**
 * 质量报废对象 sms_quality_scrap_order
 *
 * @author ltq
 * @date 2020-12-10
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "质量报废")
public class SmsQualityScrapOrderSupplierExportVo extends BaseEntity {
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
    @ExcelProperty(value = "报废单号")
    @ApiModelProperty(value = "报废单号")
    private String scrapNo;

    /**
     * 专用号
     */
    @ExcelProperty(value = "成品专用号")
    @ApiModelProperty(value = "成品专用号")
    private String productMaterialCode;

    /**
     * 专用号名称
     */
    @ExcelProperty(value = "成品专用号描述")
    @ApiModelProperty(value = "成品专用号描述")
    private String productMaterialName;

    /**
     * 供应商编码
     */
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 工厂
     */
    @ExcelProperty(value = "工厂")
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 付款公司
     */
    @ApiModelProperty(value = "公司")
    private String companyCode;

    /**
     * 工位
     */
    @ApiModelProperty(value = "工位")
    private String station;

    /**
     * 数量
     */
    @ExcelProperty(value = "报废数量")
    @ApiModelProperty(value = "报废数量")
    private BigDecimal scrapAmount;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String stuffUnit;
    /**
     * 报废单价
     */
    @ApiModelProperty(value = "报废单价")
    private BigDecimal materialPrice;

    /**
     * 结算金额
     */
    @ApiModelProperty(value = "结算费用")
    private BigDecimal settleFee;

    /**
     * 兑现金额
     */
    @ApiModelProperty(value = "兑现费用")
    private BigDecimal cashAmount;

    /**
     * SAP创单凭证
     */
    @ExcelProperty(value = "SAP过账单号")
    @ApiModelProperty(value = "SAP过账单号")
    private String postingNo;

    /**
     * SAP创单时间
     */
    @ExcelProperty(value = "SAP过账时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "SAP过账时间")
    private Date sapDate;

    /**
     * 质量部报废状态 0待提交、1供应商待确认，2供应商确认，3超时自动确认、4 质量部待审核、5小微主待审核、6小微主审核通过， 7供应商待确认(申诉驳回)、 11待结算、12结算完成、、13已兑现、14部分兑现、15未兑现' ,
     */
    @ExcelProperty(value = "报废状态",converter = QualityScrapOrderStatusConverter.class)
    @ApiModelProperty(value = "质量部报废状态 0待提交、1供应商待确认，2供应商确认，3超时自动确认、4 质量部待审核、5小微主待审核、6小微主审核通过， 7供应商待确认(申诉驳回)、 11待结算、12结算完成、、13已兑现、14部分兑现、15未兑现' ,")
    private String scrapStatus;

    /**
     * 提交时间
     */
    @ExcelProperty(value = "提交时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitDate;

    /**
     * 供应商确认时间
     */
    @ExcelProperty(value = "供应商确认时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "供应商确认时间")
    private Date supplierConfirmDate;

    /**
     * 申诉时间
     */
    @ExcelProperty(value = "申诉时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申诉时间")
    private Date complaintDate;

    /**
     * 申诉描述
     */
    @ApiModelProperty(value = "申诉描述")
    private String complaintDescription;

    /**
     * 结算单号
     */
    @ApiModelProperty(value = "结算单号")
    private String settleNo;


    /**
     * 未兑现金额
     */
    @ApiModelProperty(value = "未兑现金额")
    private BigDecimal uncashAmount;

    /**
     * 库存地点
     */
    @ApiModelProperty(value = "库存地点")
    private String sapStoreage;

    /**
     * SAP创单结果
     */
    @ApiModelProperty(value = "SAP创单结果")
    private String sapResult;

    /**
     * SAP创单备注
     */
    @ApiModelProperty(value = "SAP创单备注")
    private String sapRemark;


    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 汇率
     */
    @ApiModelProperty(value = "汇率")
    private BigDecimal rate;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

    @JsonIgnore
    @Transient
    private String beginTime;

    @JsonIgnore
    @Transient
    private String endTime;

    @Transient
    private String saveOrSubmit;

}
