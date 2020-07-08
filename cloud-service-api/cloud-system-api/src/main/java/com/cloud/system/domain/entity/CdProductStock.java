package com.cloud.system.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
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

/**
 * 成品库存主 对象 cd_product_stock
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "成品库存主表 ")
public class CdProductStock extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料号",index = 0)
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品物料描述",index = 1)
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码",index = 2)
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 在产库存
     */
    @ExcelProperty(value = "在产库存",index = 4)
    @ApiModelProperty(value = "在产库存")
    private BigDecimal stockPNum = BigDecimal.ZERO;

    /**
     * 在库库存
     */
    @ExcelProperty(value = "在库库存",index = 5)
    @ApiModelProperty(value = "在库库存")
    private BigDecimal stockWNum = BigDecimal.ZERO;

    /**
     * 在途库存
     */
    @ExcelProperty(value = "在途库存",index = 6)
    @ApiModelProperty(value = "在途库存")
    private BigDecimal stockINum = BigDecimal.ZERO;

    /**
     * 寄售不足
     */
    @ExcelProperty(value = "寄售不足",index = 8)
    @ApiModelProperty(value = "寄售不足")
    private BigDecimal stockKNum = BigDecimal.ZERO;

    /**
     * 不良品库存
     */
    @ExcelProperty(value = "不良品库存",index = 9)
    @ApiModelProperty(value = "不良品库存")
    private BigDecimal rejectsNum = BigDecimal.ZERO;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 3)
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

    /**
     * 库存总量
     */
    @ExcelProperty(value = "库存总量",index = 10)
    @ApiModelProperty(value = "库存总量")
    private BigDecimal sumNum;

}
