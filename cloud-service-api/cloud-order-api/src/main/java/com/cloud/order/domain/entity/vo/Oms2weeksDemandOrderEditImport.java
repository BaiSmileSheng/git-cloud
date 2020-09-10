package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * T+1、T+2草稿计划 导入对象
 *
 * @author cs
 * @date 2020-06-22
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Oms2weeksDemandOrderEditImport {
    private static final long serialVersionUID = 1L;




    /**
     * 订单类型
     */
    @ExcelProperty(value = "SAP订单类型")
    @NotBlank
    private String orderType;

    /**
     * 订单来源 1：内单，2：外单
     */
    @ExcelProperty(value = "订单来源")
    @NotBlank
    private String orderFrom;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料")
    @NotBlank
    private String productMaterialCode;


    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "工厂")
    @NotBlank
    private String productFactoryCode;


    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码")
    @NotBlank
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户名称")
    @NotBlank
    private String customerDesc;

    /**
     * MRP范围
     */
    @ExcelProperty(value = "MRP范围")
    @NotBlank
    private String mrpRange;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "版本")
    @NotBlank
    private String bomVersion;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    private String purchaseGroupCode;

    /**
     * 地点
     */
    @ExcelProperty(value = "接收库位")
    @NotBlank
    private String place;

    /**
     * 交付日期
     */
    @ExcelProperty(value = "交付日期")
    @DateTimeFormat("yyyy-MM-dd")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date deliveryDate;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "数量")
    @NotNull
    private Long orderNum;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    @ExcelProperty(value = "错误信息")
    private String errorMsg;
}
