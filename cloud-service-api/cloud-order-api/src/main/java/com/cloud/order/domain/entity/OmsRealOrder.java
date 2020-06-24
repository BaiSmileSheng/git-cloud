package com.cloud.order.domain.entity;

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
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * 真单对象 oms_real_order
 *
 * @author ltq
 * @date 2020-06-15
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "真单")
public class OmsRealOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    @ApiModelProperty(value = "订单号")
    private String orderCode;

    /**
     * 订单类型
     */
    @ExcelProperty(value = "订单类型")
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    /**
     * 订单来源 1：内单，2：外单
     */
    @ExcelProperty(value = "订单来源 1：内单，2：外单")
    @ApiModelProperty(value = "订单来源 1：内单，2：外单")
    private String orderFrom;

    /**
     * 订单种类 1：正常，2：追加，3：储备，4：新品
     */
    @ExcelProperty(value = "订单种类 1：正常，2：追加，3：储备，4：新品")
    @ApiModelProperty(value = "订单种类 1：正常，2：追加，3：储备，4：新品")
    private String orderClass;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料号")
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品物料描述")
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户描述")
    @ApiModelProperty(value = "客户描述")
    private String customerDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码")
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ExcelProperty(value = "生产工厂描述")
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * MRP范围
     */
    @ExcelProperty(value = "MRP范围")
    @ApiModelProperty(value = "MRP范围")
    private String mrpRange;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "BOM版本")
    @ApiModelProperty(value = "BOM版本")
    private String bomVersion;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroupCode;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "订单数量")
    @ApiModelProperty(value = "订单数量")
    private BigDecimal orderNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 交付日期
     */
    @ExcelProperty(value = "交付日期")
    @ApiModelProperty(value = "交付日期")
    private String deliveryDate;

    /**
     * 生产日期
     */
    @ExcelProperty(value = "生产日期")
    @ApiModelProperty(value = "生产日期")
    private String productDate;

    /**
     * 地点
     */
    @ExcelProperty(value = "地点")
    @ApiModelProperty(value = "地点")
    private String place;

    /**
     * 0：无需审核，1：审核中，2：审核完成
     */
    @ExcelProperty(value = "0：无需审核，1：审核中，2：审核完成")
    @ApiModelProperty(value = "0：无需审核，1：审核中，2：审核完成")
    private String auditStatus;

    /**
     * 状态 0：初始，1：已调整
     */
    @ExcelProperty(value = "状态 0：初始，1：已调整")
    @ApiModelProperty(value = "状态 0：初始，1：已调整")
    private String status;

    /**
     * 数据源 0：接口接入，1：人工导入
     */
    @ExcelProperty(value = "数据源 0：接口接入，1：人工导入")
    @ApiModelProperty(value = "数据源 0：接口接入，1：人工导入")
    private String dataSource;

    /**
     * 是否删除 0：有效，1：删除
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
