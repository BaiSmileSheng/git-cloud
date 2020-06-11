package com.cloud.settle.domain.entity;

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


/**
 * 质量索赔扣款类型和扣款标准对象 sms_quality_type
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "质量索赔扣款类型和扣款标准")
public class SmsQualityType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 索赔类型
     */
    @ExcelProperty(value = "索赔类型")
    @ApiModelProperty(value = "索赔类型")
    private String claimType;

    /**
     * 索赔类型展示
     */
    @Transient
    @ApiModelProperty(value = "索赔类型展示")
    private String claimTypeShow;

    /**
     * 删除状态 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 父id
     */
    @ExcelProperty(value = "父id")
    @ApiModelProperty(value = "父id")
    private Long parentId;

}
