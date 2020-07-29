package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 滚动计划需求操作 导入对象模板
 *
 * @author cs
 * @date 2020-06-16
 */
@ExcelIgnoreUnannotated
@Data
public class OmsDemandOrderGatherEditImportTemplete {
    private static final long serialVersionUID = 1L;


    /**
     * 订单来源 1：内单，2：外单
     */
    @ExcelProperty(value = "订单来源")
    private String orderFrom;

    /**
     * 订单类型
     */
    @ExcelProperty(value = "SAP订单类型")
    private String orderType;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料")
    private String productMaterialCode;


    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "工厂")
    private String productFactoryCode;


    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户名称")
    private String customerDesc;

    /**
     * MRP范围
     */
    @ExcelProperty(value = "MRP范围")
    private String mrpRange;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "版本")
    private String bomVersion;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "数量")
    private Long orderNum;

    /**
     * 交付日期
     */
    @ExcelProperty(value = "交付日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date deliveryDate;

    /**
     * 地点
     */
    @ExcelProperty(value = "地点")
    private String place;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;
}
