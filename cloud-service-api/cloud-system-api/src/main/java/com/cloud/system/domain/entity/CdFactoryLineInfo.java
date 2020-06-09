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


/**
 * 工厂线体关系 对象 cd_factory_line_info
 *
 * @author cs
 * @date 2020-06-01
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "工厂线体关系 ")
public class CdFactoryLineInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 供应商编码
     */
    @ExcelProperty(value = "供应商编码")
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;

    /**
     * 供应商描述
     */
    @ExcelProperty(value = "供应商描述")
    @ApiModelProperty(value = "供应商描述")
    private String supplierDesc;

    /**
     * 工厂编码
     */
    @ExcelProperty(value = "工厂编码")
    @ApiModelProperty(value = "工厂编码")
    private String factoryCode;

    /**
     * 工厂描述
     */
    @ExcelProperty(value = "工厂描述")
    @ApiModelProperty(value = "工厂描述")
    private String factoryDesc;

    /**
     * 线体编码
     */
    @ExcelProperty(value = "线体编码")
    @ApiModelProperty(value = "线体编码")
    private String produceLineCode;

    /**
     * 线体描述
     */
    @ExcelProperty(value = "线体描述")
    @ApiModelProperty(value = "线体描述")
    private String produceLineDesc;

    /**
     * 分公司主管
     */
    @ExcelProperty(value = "分公司主管")
    @ApiModelProperty(value = "分公司主管")
    private String branchOffice;

    /**
     * 分公司主管邮箱
     */
    @ExcelProperty(value = "分公司主管邮箱")
    @ApiModelProperty(value = "分公司主管邮箱")
    private String branchOfficeEmail;

    /**
     * 班长
     */
    @ExcelProperty(value = "班长")
    @ApiModelProperty(value = "班长")
    private String monitor;

    /**
     * 班长邮箱
     */
    @ExcelProperty(value = "班长邮箱")
    @ApiModelProperty(value = "班长邮箱")
    private String monitorEmail;

    /**
     * 属性 1：自制，2：工序，3：OEM
     */
    @ExcelProperty(value = "属性 1：自制，2：工序，3：OEM")
    @ApiModelProperty(value = "属性 1：自制，2：工序，3：OEM")
    private String attribute;

    /**
     * 产品定员
     */
    @ExcelProperty(value = "产品定员")
    @ApiModelProperty(value = "产品定员")
    private int productQuota;

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
