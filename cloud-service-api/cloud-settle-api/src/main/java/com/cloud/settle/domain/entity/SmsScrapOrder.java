package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.IsEntityConverter;
import com.cloud.settle.converter.IsPayConverter;
import com.cloud.settle.converter.OutsourceWayConverter;
import com.cloud.settle.converter.ScrapOrderStatusConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 报废申请 对象 sms_scrap_order
 *
 * @author cs
 * @date 2020-05-29
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "报废申请 ")
public class SmsScrapOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 报废单号
     */
    @ExcelProperty(value = "报废单号",index = 0)
    @ApiModelProperty(value = "报废单号")
    private String scrapNo;

    /**
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号",index = 1)
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码",index = 2)
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称",index = 3)
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 报废类型  0半成品  1成品
     */
    @ApiModelProperty(value = "报废类型")
    @ExcelProperty(value = "报废类型",index = 4,converter = OutsourceWayConverter.class)
    private String scrapType;

    /**
     * 专用号
     */
    @ExcelProperty(value = "成品专用号",index = 5)
    @ApiModelProperty(value = "专用号")
    private String productMaterialCode;

    /**
     * 专用号描述
     */
    @ExcelProperty(value = "成品专用号描述",index = 6)
    @ApiModelProperty(value = "专用号描述")
    private String productMaterialName;

    /**
     * 半成品专用号
     */
    @ExcelProperty(value = "半成品专用号",index = 7)
    private String semiFinishedCode;

    /**
     * 半成品专用号描述
     */
    @ExcelProperty(value = "半成品专用号描述",index = 8)
    private String semiFinishedName;

    /**
     * 是否买单 0买单  1不买单
     */
    @ExcelProperty(value = "是否买单",index = 9,converter = IsPayConverter.class)
    private String isPay;

    /**
     * 有无实物 0有 1没有
     */
    @ExcelProperty(value = "有无实物",index = 10,converter = IsEntityConverter.class)
    private String isEntity;

    /**
     * 报废数量
     */
    @ExcelProperty(value = "报废数量",index = 11)
    @ApiModelProperty(value = "报废数量")
    private Integer scrapAmount;

    /**
     * SAP过账单号
     */
    @ExcelProperty(value = "SAP过账单号",index = 12)
    @ApiModelProperty(value = "SAP过账单号")
    private String postingNo;

    /**
     * 兑现加工费
     */
    @ApiModelProperty(value = "兑现加工费")
    private BigDecimal machiningPrice;

    /**
     * 报废状态 0待提交、1业务科待审核、2业务科驳回、3 SAP过账成功、4 SAP过账失败、11待结算、12结算完成、13已兑现、14部分兑现、15未兑现
     */
    @ExcelProperty(value = "报废状态",index = 13,converter = ScrapOrderStatusConverter.class)
    @ApiModelProperty(value = "报废状态 0待提交、1业务科待审核、2业务科驳回、3 SAP过账成功、4 SAP过账失败、11待结算、12结算完成、13已兑现、14部分兑现、15未兑现")
    private String scrapStatus;

    /**
     * 提交时间
     */
    @ExcelProperty(value = "提交时间",index = 14)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitDate;

    /**
     * 工厂
     */
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 付款公司
     */
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * SAP过账时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "SAP过账时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sapTransDate;

    /**
     * SAP创单备注
     */
    @ApiModelProperty(value = "SAP创单备注")
    private String sapRemark;

    /**
     * 计量单位
     */
    @ApiModelProperty(value = "计量单位")
    private String measureUnit;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;

    /**
     * 报废单价
     */
    @ApiModelProperty(value = "报废单价")
    private BigDecimal materialPrice;

    /**
     * 报废金额
     */
    @ApiModelProperty(value = "报废金额")
    private BigDecimal scrapPrice;

    /**
     * 结算单号
     */
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 索赔金额
     */
    @ApiModelProperty(value = "索赔金额")
    private BigDecimal settleFee;

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
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 流程实例ID
     */
    @Transient
    private String procDefId;

    /**
     * 流程实例名称
     */
    @Transient
    private String procName;

}
