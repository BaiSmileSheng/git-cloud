package com.cloud.order.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.order.converter.OrderFromConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;

/**
 * 滚动计划需求 对象 oms_demand_order_gather
 *
 * @author cs
 * @date 2020-06-12
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "滚动计划需求 ")
public class OmsDemandOrderGather extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 需求订单号
     */
    @ApiModelProperty(value = "需求订单号")
    private String demandOrderCode;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    /**
     * 订单来源 1：内单，2：外单
     */
    @ExcelProperty(value = "订单来源",index = 0,converter = OrderFromConverter.class)
    @ApiModelProperty(value = "订单来源 1：内单，2：外单")
    private String orderFrom;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料",index = 1)
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品物料描述",index = 2)
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "工厂",index = 3)
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码",index = 4)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户名称",index = 5)
    @ApiModelProperty(value = "客户描述")
    private String customerDesc;

    /**
     * MRP范围
     */
    @ExcelProperty(value = "MRP范围",index = 6)
    @ApiModelProperty(value = "MRP范围")
    private String mrpRange;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "版本",index = 7)
    @ApiModelProperty(value = "BOM版本")
    private String bomVersion;

    /**
     * 采购组
     */
    @ApiModelProperty(value = "采购组")
    private String purchaseGroupCode;

    /**
     * 地点
     */
    @ExcelProperty(value = "地点",index = 10)
    @ApiModelProperty(value = "地点")
    private String place;

    /**
     * 交付日期
     */
    @ExcelProperty(value = "交付日期",index = 9)
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "交付日期")
    private Date deliveryDate;

    /**
     * 年度 交货日期年度
     */
    @ApiModelProperty(value = "年度 交货日期年度")
    private String year;

    /**
     * 周数 交货日期周数
     */
    @ExcelProperty(value = "周数",index = 11)
    @ApiModelProperty(value = "周数 交货日期周数")
    private String weeks;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "数量",index = 8)
    @ApiModelProperty(value = "订单数量")
    private Long orderNum;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 数据版本 年度-周数
     */
    @ApiModelProperty(value = "数据版本 年度-周数")
    private String version;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

}
