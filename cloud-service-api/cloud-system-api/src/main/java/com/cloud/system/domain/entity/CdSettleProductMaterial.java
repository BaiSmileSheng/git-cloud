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


/**
 * 物料号和加工费号对应关系 对象 cd_settle_product_material
 *
 * @author cs
 * @date 2020-06-05
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "物料号和加工费号对应关系 ")
public class CdSettleProductMaterial extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 成品物料编码
     */
    @ExcelProperty(value = "成品物料编码")
    @ApiModelProperty(value = "成品物料编码")
    private String productMaterialCode;

    /**
     * 加工费号
     */
    @ExcelProperty(value = "加工费号")
    @ApiModelProperty(value = "加工费号")
    private String rawMaterialCode;

    /**
     * 委外方式
     */
    @ExcelProperty(value = "委外方式")
    @ApiModelProperty(value = "委外方式")
    private String outsourceWay;

    /**
     * 删除标志
     */
    private String delFlag;

}
