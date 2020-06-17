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
 * 超期库存 对象 cd_product_overdue
 *
 * @author lihongxia
 * @date 2020-06-17
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "超期库存 ")
public class CdProductOverdue extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 超期物料号
     */
    @ExcelProperty(value = "超期物料号",index = 0)
    @ApiModelProperty(value = "超期物料号")
    private String productMaterialCode;

    /**
     * 超期物料描述
     */
    @ExcelProperty(value = "超期物料描述",index = 1)
    @ApiModelProperty(value = "超期物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码",index = 2)
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
