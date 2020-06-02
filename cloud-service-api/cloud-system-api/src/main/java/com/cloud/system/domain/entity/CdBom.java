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
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * bom清单数据 对象 cd_bom
 *
 * @author cs
 * @date 2020-06-01
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "bom清单数据 ")
public class CdBom extends BaseEntity {
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
     * 工厂编码
     */
    @ExcelProperty(value = "工厂编码")
    @ApiModelProperty(value = "工厂编码")
    private String factoryCode;

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
     * 基本数量
     */
    @ExcelProperty(value = "基本数量")
    @ApiModelProperty(value = "基本数量")
    private Long basicNum;

    /**
     * 单耗
     */
    @ExcelProperty(value = "单耗")
    @ApiModelProperty(value = "单耗")
    private BigDecimal bomNum;

    /**
     * 成品单位
     */
    @ExcelProperty(value = "成品单位")
    @ApiModelProperty(value = "成品单位")
    private String productUnit;

    /**
     * 组件单位
     */
    @ExcelProperty(value = "组件单位")
    @ApiModelProperty(value = "组件单位")
    private String componentUnit;

    /**
     * 版本
     */
    @ExcelProperty(value = "版本")
    @ApiModelProperty(value = "版本")
    private String version;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroup;

    /**
     * 仓储点
     */
    @ExcelProperty(value = "仓储点")
    @ApiModelProperty(value = "仓储点")
    private String storagePoint;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    @ApiModelProperty(value = "备注")
    private String remarks;

}
