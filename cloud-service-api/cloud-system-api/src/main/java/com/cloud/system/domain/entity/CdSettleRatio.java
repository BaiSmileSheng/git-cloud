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
 * 结算索赔系数 对象 cd_settle_ratio
 *
 * @author cs
 * @date 2020-06-04
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "结算索赔系数 ")
public class CdSettleRatio extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 索赔类型 1.物耗申请  2.报废申请
     */
    @ExcelProperty(value = "索赔类型 1.物耗申请  2.报废申请")
    @ApiModelProperty(value = "索赔类型 1.物耗申请  2.报废申请")
    private String claimType;

    /**
     * 索赔系数
     */
    @ExcelProperty(value = "索赔系数")
    @ApiModelProperty(value = "索赔系数")
    private BigDecimal ratio;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

}
