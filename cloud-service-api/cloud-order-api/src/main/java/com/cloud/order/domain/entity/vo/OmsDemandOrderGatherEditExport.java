package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 滚动计划需求操作 导出对象
 *
 * @author cs
 * @date 2020-06-16
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OmsDemandOrderGatherEditExport {
    private static final long serialVersionUID = 1L;


    /**
     * 成品物料号
     */
    @ExcelProperty(value = "专用号",index = 0)
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "专用号描述",index = 1)
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂",index = 4)
    private String productFactoryCode;


    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码",index = 2)
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户名称",index = 3)
    private String customerDesc;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 5)
    private String unit;

    @ExcelProperty(value = "T+3周")
    private Long t3Num;

    @ExcelProperty(value = "T+4周")
    private Long t4Num;

    @ExcelProperty(value = "T+5周")
    private Long t5Num;

    @ExcelProperty(value = "T+6周")
    private Long t6Num;

    @ExcelProperty(value = "T+7周")
    private Long t7Num;

    @ExcelProperty(value = "T+8周")
    private Long t8Num;

    @ExcelProperty(value = "T+9周")
    private Long t9Num;

    @ExcelProperty(value = "T+10周")
    private Long t10Num;

    @ExcelProperty(value = "T+11周")
    private Long t11Num;

    @ExcelProperty(value = "T+12周")
    private Long t12Num;

    @ExcelProperty(value = "T+13周")
    private Long t13Num;

    @ExcelProperty(value = "需求总量")
    private Long totalDemandNum;

    @ExcelProperty(value = "库存量")
    private BigDecimal stockNum;

}
