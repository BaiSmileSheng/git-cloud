package com.cloud.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;


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
public class CdProductOverdueExportVo{
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
    @ExcelProperty(value = "超期工厂编码",index = 2)
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注",index = 3)
    private String remark;

    /**
     * 创建者
     */
    @ExcelProperty(value = "创建人",index = 4)
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间",index = 5)
    private Date createTime;

    /**
     * 更新者
     */
    @ExcelProperty(value = "更新人",index = 6)
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "更新时间",index = 7)
    private Date updateTime;



}
