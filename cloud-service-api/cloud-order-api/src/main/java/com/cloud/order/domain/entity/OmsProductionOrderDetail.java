package com.cloud.order.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.order.converter.ProductionOrderDetailStatusConverter;
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
 * 排产订单明细 对象 oms_production_order_detail
 *
 * @author ltq
 * @date 2020-06-19
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "排产订单明细 ")
public class OmsProductionOrderDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 排产订单号
     */
    @ApiModelProperty(value = "排产订单号")
    private String productOrderCode;


    /**
     * 原材料号
     */
    @ExcelProperty(value = "原材料号",index = 0)
    @ApiModelProperty(value = "原材料号")
    private String materialCode;

    /**
     * 原材料描述
     */
    @ExcelProperty(value = "原材料描述",index = 1)
    @ApiModelProperty(value = "原材料描述")
    private String materialDesc;
    /**
     * 生产工厂
     */
    @ExcelProperty(value = "生产工厂",index = 2)
    @ApiModelProperty(value = "生产工厂")
    private String productFactoryCode;

    /**
     * 单耗
     */
    @ApiModelProperty(value = "单耗")
    private BigDecimal bomNum;

    /**
     * 基本数量
     */
    @ApiModelProperty(value = "基本数量")
    private BigDecimal basicNum;

    /**
     * 原材料排产量
     */
    @ExcelProperty(value = "原材料排产量",index = 3)
    @ApiModelProperty(value = "原材料排产量")
    private BigDecimal rawMaterialProductNum;
    /**
     * 基本开始日期
     */
    @ExcelProperty(value = "基本开始日期",index = 4)
    @ApiModelProperty(value = "基本开始日期")
    private String productStartDate;
    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 5)
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * BOM版本
     */
    @ApiModelProperty(value = "BOM版本")
    private String bomVersion;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组",index = 6)
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 仓储点
     */
    @ApiModelProperty(value = "仓储点")
    private String storagePoint;

    /**
     * 状态 0：未确认，1：已确认，2：反馈中
     */
    @ExcelProperty(value = "状态",index = 7,converter = ProductionOrderDetailStatusConverter.class )
    @ApiModelProperty(value = "状态 0：未确认，1：已确认，2：反馈中")
    private String status;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;
    /**
     * 生产工厂权限，逗号隔开
     */
    @Transient
    @ApiModelProperty(value = "生产工厂权限，逗号隔开")
    private String productFactoryQuery;
    /**
     * 采购组权限，逗号隔开
     */
    @Transient
    @ApiModelProperty(value = "采购组权限，逗号隔开")
    private String purchaseGroupQuery;
    /**
     * 查询开始日期
     */
    @Transient
    @ApiModelProperty(value = "查询开始日期")
    private String checkStartDate;
    /**
     * 查询结束日期
     */
    @Transient
    @ApiModelProperty(value = "查询结束日期")
    private String checkEndDate;
    /**
     * 实体list
     */
    @Transient
    @ApiModelProperty(value = "实体list")
    private List<OmsProductionOrderDetail> orderDetailList;
    /**
     * 确认时间
     */
    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;
    /**
     * 确认人
     */
    @ApiModelProperty(value = "确认人")
    private String confirmBy;
}
