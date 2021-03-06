package com.cloud.settle.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.settle.converter.OutsourceWayConverter;
import com.cloud.settle.converter.SettleInfoStatusConverter;
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
 * 加工费结算 对象 sms_settle_info
 *
 * @author cs
 * @date 2020-06-04
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "加工费结算 ")
public class SmsSettleInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 线体号
     */
    @ExcelProperty(value = "线体号",index = 2)
    @ApiModelProperty(value = "线体号")
    private String lineNo;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码",index = 0)
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @ExcelProperty(value = "供应商名称",index = 1)
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    /**
     * 工厂
     */
    @ApiModelProperty(value = "工厂")
    private String factoryCode;

    /**
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号",index = 3)
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 订单状态 1未关单、2 已关单、11待结算、12 结算完成
     */
    @ExcelProperty(value = "订单状态",index = 16,converter = SettleInfoStatusConverter.class)
    @ApiModelProperty(value = "订单状态 1未关单、2 已关单、11待结算、12 结算完成")
    private String orderStatus;

    /**
     * 付款公司
     */
    @ApiModelProperty(value = "付款公司")
    private String companyCode;

    /**
     * 专用号
     */
    @ExcelProperty(value = "专用号",index = 4)
    @ApiModelProperty(value = "专用号")
    private String productMaterialCode;

    /**
     * 专用号名称
     */
    @ExcelProperty(value = "专用号名称",index = 5)
    @ApiModelProperty(value = "专用号名称")
    private String productMaterialName;

    /**
     * BOM版本号
     */
    @ExcelProperty(value = "BOM版本号",index = 12)
    @ApiModelProperty(value = "BOM版本号")
    private String bomVersion;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "订单数量",index = 6)
    @ApiModelProperty(value = "订单数量")
    private Integer orderAmount;

    /**
     * 入库数量
     */
    @ExcelProperty(value = "入库数量",index = 7)
    @ApiModelProperty(value = "入库数量")
    private Integer confirmAmont;

    /**
     * 差异数量
     */
    @Transient
    @ExcelProperty(value = "差异数量",index = 8)
    private Integer differenceAmont;

    /**
     * 委外方式 0：半委外，1：全委外
     */
    @ExcelProperty(value = "加工承揽方式",index = 13,converter = OutsourceWayConverter.class)
    @ApiModelProperty(value = "委外方式 0：半委外，1：全委外")
    private String outsourceWay;

    /**
     * 不含税单价
     */
    @ExcelProperty(value = "不含税单价",index = 14)
    @ApiModelProperty(value = "不含税单价")
    private BigDecimal machiningPrice;

    /**
     * 不含税金额
     */
    @ExcelProperty(value = "不含税金额",index = 15)
    @ApiModelProperty(value = "不含税金额")
    private BigDecimal settlePrice;

    /**
     * 结算单号
     */
    @ApiModelProperty(value = "结算单号")
    private String settleNo;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 基本开始日期
     */
    @ExcelProperty(value = "基本开始日期",index = 9)
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "基本开始日期")
    private Date productStartDate;

    /**
     * 基本结束日期
     */
    @ExcelProperty(value = "基本结束日期",index = 10)
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "基本结束日期")
    private Date productEndDate;

    /**
     * 实际结束日期
     */
    @ExcelProperty(value = "实际结束日期",index = 11)
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "实际结束日期")
    private Date actualEndDate;

    @Transient
    @ApiModelProperty(value = "1:基本开始时间,2:基本结束时间,3:实际结束时间")
    private String timeType;

    @Transient  //tk 不操作字段
    @ApiModelProperty(value = "合计金额")
    private BigDecimal totalMoney;

    @Transient  //tk 不操作字段
    @ApiModelProperty(value = "在本月的第几天")
    private int dayNum;


}
