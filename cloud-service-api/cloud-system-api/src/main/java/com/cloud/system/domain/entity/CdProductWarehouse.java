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
 * 成品库存在库明细 对象 cd_product_warehouse
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "成品库存在库明细 ")
public class CdProductWarehouse extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 0:良品;1:不良品
     */
    @ApiModelProperty(value = "0:良品;1:不良品")
    private String stockType;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料号",index = 0)
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码",index = 1)
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 库位
     */
    @ExcelProperty(value = "库位",index = 2)
    @ApiModelProperty(value = "库位")
    private String storehouse;

    /**
     * 在库量
     */
    @ExcelProperty(value = "在库量",index = 3)
    @ApiModelProperty(value = "在库量")
    private BigDecimal warehouseNum;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
