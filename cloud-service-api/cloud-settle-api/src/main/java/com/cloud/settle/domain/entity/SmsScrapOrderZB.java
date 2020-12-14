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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@ApiModel(value = "报废申请总部 ")
public class SmsScrapOrderZB extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 报废单号
     */
    @ExcelProperty(value = "报废单号",index = 0)
    private String scrapNo;

    /**
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号",index = 1)
    private String productOrderCode;

    /**
     * 报废类型  0半成品  1成品
     */
    @ExcelProperty(value = "报废类型",index = 2,converter = OutsourceWayConverter.class)
    private String scrapType;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码",index = 3)
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称",index = 4)
    private String supplierName;

    /**
     * 专用号
     */
    @ExcelProperty(value = "成品专用号",index = 5)
    private String productMaterialCode;

    /**
     * 专用号描述
     */
    @ExcelProperty(value = "成品专用号描述",index = 6)
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
     * 报废数量
     */
    @ExcelProperty(value = "报废数量",index = 9)
    private Integer scrapAmount;

    /**
     * 报废单价
     */
    @ExcelProperty(value = "报废数量",index = 10)
    private BigDecimal materialPrice;

    /**
     * 报废金额
     */
    @ExcelProperty(value = "报废金额",index = 11)
    private BigDecimal scrapPrice;

    /**
     * 兑现加工费
     */
    @ExcelProperty(value = "兑现加工费",index = 12)
    private BigDecimal machiningPrice;

    /**
     * 结算单号
     */
    @ExcelProperty(value = "结算单号",index = 13)
    private String settleNo;

    /**
     * 索赔金额
     */
    @ExcelProperty(value = "索赔金额",index = 14)
    private BigDecimal settleFee;

    /**
     * 兑现金额
     */
    @ExcelProperty(value = "兑现金额",index = 15)
    private BigDecimal cashAmount;

    /**
     * 未兑现金额
     */
    @ExcelProperty(value = "未兑现金额",index = 16)
    private BigDecimal uncashAmount;

    /**
     * 是否买单 0买单  1不买单
     */
    @ExcelProperty(value = "是否买单",index = 17,converter = IsPayConverter.class)
    private String isPay;

    /**
     * 有无实物 0有 1没有
     */
    @ExcelProperty(value = "有无实物",index = 18,converter = IsEntityConverter.class)
    private String isEntity;

    /**
     * 报废状态 0待提交、1业务科待审核、2业务科驳回、3 SAP过账成功、4 SAP过账失败、11待结算、12结算完成、13已兑现、14部分兑现、15未兑现
     */
    @ExcelProperty(value = "报废状态",index = 19,converter = ScrapOrderStatusConverter.class)
    private String scrapStatus;

    /**
     * SAP过账单号
     */
    @ExcelProperty(value = "SAP过账单号",index = 20)
    private String postingNo;

    /**
     * SAP过账时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "SAP过账时间",index = 21)
    private Date sapTransDate;

    /**
     * SAP创单备注
     */
    @ExcelProperty(value = "报废金额",index = 22)
    private String sapRemark;


    /**
     * 提交时间
     */
    @ExcelProperty(value = "提交时间",index = 23)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitDate;

}
