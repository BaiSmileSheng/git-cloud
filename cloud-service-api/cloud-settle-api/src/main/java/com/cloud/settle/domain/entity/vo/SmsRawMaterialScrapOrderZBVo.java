package com.cloud.settle.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.RawScrapOrderIsCheckConverter;
import com.cloud.settle.converter.RawScrapOrderIsMaterialObjectConverter;
import com.cloud.settle.converter.RawScrapOrderStatusConverter;
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
 * 原材料报废申请对象 sms_raw_material_scrap_order
 *
 * @author ltq
 * @date 2020-12-07
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "原材料报废申请")
public class SmsRawMaterialScrapOrderZBVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 原材料报废单号
     */
    @ExcelProperty(value = "原材料报废单号")
    @ApiModelProperty(value = "原材料报废单号")
    private String rawScrapNo;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称")
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 原材料物料号
     */
    @ExcelProperty(value = "原材料物料号")
    @ApiModelProperty(value = "原材料物料号")
    private String rawMaterialCode;

    /**
     * 原材料物料名称
     */
    @ExcelProperty(value = "原材料物料名称")
    @ApiModelProperty(value = "原材料物料名称")
    private String rawMaterialName;

    /**
     * 工厂
     */
    @ExcelProperty(value = "工厂")
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 公司
     */
    @ExcelProperty(value = "公司")
    @ApiModelProperty(value = "公司")
    private String componyCode;

    /**
     * 工位
     */
    @ExcelProperty(value = "工位")
    @ApiModelProperty(value = "工位")
    private String station;

    /**
     * 报废订单号
     */
    @ExcelProperty(value = "报废订单号")
    @ApiModelProperty(value = "报废订单号")
    private String scrapOrderCode;

    /**
     * 数量
     */
    @ExcelProperty(value = "数量")
    @ApiModelProperty(value = "数量")
    private BigDecimal scrapNum;

    /**
     * 计量单位
     */
    @ExcelProperty(value = "计量单位")
    @ApiModelProperty(value = "计量单位")
    private String measureUnit;

    /**
     * 评估类型
     */
    @ExcelProperty(value = "评估类型")
    @ApiModelProperty(value = "评估类型")
    private String assessmentType;

    /**
     * 是否买单
     */
    @ExcelProperty(value = "是否买单",converter = RawScrapOrderIsCheckConverter.class)
    @ApiModelProperty(value = "是否买单")
    private String isCheck;

    /**
     * 有无实物
     */
    @ExcelProperty(value = "有无实物",converter = RawScrapOrderIsMaterialObjectConverter.class)
    @ApiModelProperty(value = "有无实物")
    private String isMaterialObject;

    /**
     * SAP过账单号
     */
    @ExcelProperty(value = "SAP过账单号")
    @ApiModelProperty(value = "SAP过账单号")
    private String postingNo;

    /**
     * SAP过账时间
     */
    @ExcelProperty(value = "SAP过账时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "SAP过账时间")
    private Date sapTransDate;

    /**
     * SAP创单备注
     */
    @ExcelProperty(value = "SAP创单备注")
    @ApiModelProperty(value = "SAP创单备注")
    private String sapRemark;

    /**
     * 原材料报废单价
     */
    @ApiModelProperty(value = "原材料报废单价")
    private BigDecimal rawMaterialPrice;

    /**
     * 原材料报废金额
     */
    @ApiModelProperty(value = "原材料报废金额")
    private BigDecimal scrapPrice;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 价格单位
     */
    @ApiModelProperty(value = "价格单位")
    private BigDecimal priceUnit;

    /**
     * 兑现加工费
     */
    @ApiModelProperty(value = "兑现加工费")
    private BigDecimal machiningPrice;

    /**
     * 结算单号
     */
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 兑现金额
     */
    @ApiModelProperty(value = "兑现金额")
    private BigDecimal cashAmount;

    /**
     * 未兑现金额
     */
    @ApiModelProperty(value = "未兑现金额")
    private BigDecimal uncashAmount;

    /**
     * 报废状态
     */
    @ExcelProperty(value = "报废状态",converter = RawScrapOrderStatusConverter.class)
    @ApiModelProperty(value = "报废状态")
    private String scrapStatus;

    /**
     * 提交时间
     */
    @ExcelProperty(value = "提交时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitDate;


}
