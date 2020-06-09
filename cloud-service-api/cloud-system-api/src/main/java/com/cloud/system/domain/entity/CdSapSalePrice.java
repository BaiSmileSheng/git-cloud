package com.cloud.system.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 成品销售价格 对象 cd_sap_sale_price
 *
 * @author cs
 * @date 2020-06-03
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "成品销售价格 ")
public class CdSapSalePrice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 条件类型
     */
    @ExcelProperty(value = "条件类型")
    @ApiModelProperty(value = "条件类型")
    private String conditionsType;

    /**
     * 销售组织
     */
    @ExcelProperty(value = "销售组织")
    @ApiModelProperty(value = "销售组织")
    private String marketingOrganization;

    /**
     * 物料
     */
    @ExcelProperty(value = "物料")
    @ApiModelProperty(value = "物料")
    private String materialCode;

    /**
     * 有效期从
     */
    @ExcelProperty(value = "有效期从")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期从")
    private Date beginDate;

    /**
     * 有效期到
     */
    @ExcelProperty(value = "有效期到")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期到")
    private Date endDate;

    /**
     * 定价记录号
     */
    @ExcelProperty(value = "定价记录号")
    @ApiModelProperty(value = "定价记录号")
    private String pricingRecordNo;

    /**
     * 价格
     */
    @ExcelProperty(value = "价格")
    @ApiModelProperty(value = "价格")
    private String salePrice;

    /**
     * 条件货币
     */
    @ExcelProperty(value = "条件货币")
    @ApiModelProperty(value = "条件货币")
    private String conditionsMonetary;

    /**
     * 定价单位
     */
    @ExcelProperty(value = "定价单位")
    @ApiModelProperty(value = "定价单位")
    private String unitPricing;

    /**
     * 计量单位
     */
    @ExcelProperty(value = "计量单位")
    @ApiModelProperty(value = "计量单位")
    private String measureUnit;

    /**
     * 删除标识符
     */
    @ExcelProperty(value = "删除标识符")
    @ApiModelProperty(value = "删除标识符")
    private String sapDelFlag;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 根据专用号和销售组织分组
     */
    @JsonIgnore //swagger 不显示字段
    @Transient  //tk 不操作字段
    private String mapKey;

}
