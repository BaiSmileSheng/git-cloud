package com.cloud.system.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 用户和数据权限关联对象 sys_user_scope
 *
 * @author cs
 * @date 2020-05-02
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户和数据权限关联")
public class SysUserScope {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     *  数据权限ID
     */
    @ExcelProperty(value = " 数据权限ID")
    @ApiModelProperty(value = " 数据权限ID")
    private String dataScopeId;

    /**
     * 接收用户物料权限时物料权限ID
     */
    @Transient
    private String[] scopes;
}
