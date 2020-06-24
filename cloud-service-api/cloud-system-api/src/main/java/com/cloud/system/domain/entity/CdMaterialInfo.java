package com.cloud.system.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料信息 对象 cd_material_info
 *
 * @author ltq
 * @date 2020-06-01
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "物料信息 ")
public class CdMaterialInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 物料号
     */
    @ExcelProperty(value = "物料号")
    @ApiModelProperty(value = "物料号")
    private String materialCode;

    /**
     * 物料描述
     */
    @ExcelProperty(value = "物料描述")
    @ApiModelProperty(value = "物料描述")
    private String materialDesc;

    /**
     * 物料类型
     */
    @ExcelProperty(value = "物料类型")
    @ApiModelProperty(value = "物料类型")
    private String materialType;

    /**
     * 基本单位
     */
    @ExcelProperty(value = "基本单位")
    @ApiModelProperty(value = "基本单位")
    private String primaryUom;

    /**
     * 物料组
     */
    @ExcelProperty(value = "物料组")
    @ApiModelProperty(value = "物料组")
    private String mtlGroupCode;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroupCode;

    /**
     * 工厂
     */
    @ExcelProperty(value = "工厂")
    @ApiModelProperty(value = "工厂")
    private String plantCode;

    /**
     * 舍入值
     */
    @ExcelProperty(value = "舍入值")
    @ApiModelProperty(value = "舍入值")
    private BigDecimal roundingQuantit;

    /**
     * 最后更新时间
     */
    @ExcelProperty(value = "最后更新时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后更新时间")
    private Date lastUpdate;

    /**
     * UPH节拍
     */
    @ExcelProperty(value = "UPH节拍")
    @ApiModelProperty(value = "UPH节拍")
    private BigDecimal uph;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

}
