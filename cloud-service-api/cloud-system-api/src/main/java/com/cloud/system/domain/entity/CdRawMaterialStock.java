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
 * 原材料库存 对象 cd_raw_material_stock
 *
 * @author ltq
 * @date 2020-06-05
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "原材料库存 ")
public class CdRawMaterialStock extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 原材料物料号
     */
    @ExcelProperty(value = "原材料物料号")
    @ApiModelProperty(value = "原材料物料号")
    private String rawMaterialCode;

    /**
     * 原材料物料描述
     */
    @ExcelProperty(value = "原材料物料描述")
    @ApiModelProperty(value = "原材料物料描述")
    private String rawMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码")
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 仓储点
     */
    @ExcelProperty(value = "仓储点")
    @ApiModelProperty(value = "仓储点")
    private String storagePoint;

    /**
     * 可用库存
     */
    @ExcelProperty(value = "可用库存")
    @ApiModelProperty(value = "可用库存")
    private BigDecimal currentStock;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
