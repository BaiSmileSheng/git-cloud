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

/**
 *  数据权限对象 sys_data_scope
 *
 * @author cs
 * @date 2020-05-02
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = " 数据权限")
public class SysDataScope extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Id
    private String id;

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
     * 父id
     */
    @ExcelProperty(value = "父id")
    @ApiModelProperty(value = "父id")
    private String parentId;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    /**
     * 数据权限状态（0正常 1停用）
     */
    @ExcelProperty(value = "数据权限状态")
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;

}
