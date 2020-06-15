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

import tk.mybatis.mapper.annotation.KeySql;

import java.math.BigDecimal;

/**
 * 成品库存在产明细 对象 cd_product_in_production
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "成品库存在产明细 ")
public class CdProductInProduction extends BaseEntity {
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
     * 在产版本
     */
    @ExcelProperty(value = "在产版本")
    @ApiModelProperty(value = "在产版本")
    private String inProductionVersion;

    /**
     * 在产量
     */
    @ExcelProperty(value = "在产量")
    @ApiModelProperty(value = "在产量")
    private BigDecimal inProductionNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
