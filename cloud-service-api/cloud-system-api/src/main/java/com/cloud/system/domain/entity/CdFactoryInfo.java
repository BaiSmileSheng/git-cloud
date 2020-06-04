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
 * 工厂信息 对象 cd_factory_info
 *
 * @author cs
 * @date 2020-06-03
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "工厂信息 ")
public class CdFactoryInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 公司V码
     */
    @ExcelProperty(value = "公司V码")
    @ApiModelProperty(value = "公司V码")
    private String companyCodeV;

    /**
     * 公司编码
     */
    @ExcelProperty(value = "公司编码")
    @ApiModelProperty(value = "公司编码")
    private String companyCode;

    /**
     * 公司描述
     */
    @ExcelProperty(value = "公司描述")
    @ApiModelProperty(value = "公司描述")
    private String companyDesc;

    /**
     * 工厂编码
     */
    @ExcelProperty(value = "工厂编码")
    @ApiModelProperty(value = "工厂编码")
    private String factoryCode;

    /**
     * 采购组织
     */
    @ExcelProperty(value = "采购组织")
    @ApiModelProperty(value = "采购组织")
    private String purchaseOrg;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;


}
