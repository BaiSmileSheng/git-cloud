package com.cloud.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.system.converter.OutSourceTypeConverter;
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
 * 物料号和加工费号对应关系 对象 cd_settle_product_material
 *
 * @author lihongxia
 * @date 2020-07-16
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "物料号和加工费号对应关系 ")
public class CdSettleProductMaterialExportVo {
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
    @ExcelProperty(value = "专用号",index = 0)
    @ApiModelProperty(value = "成品物料编码")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "专用号描述",index = 1)
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 加工费号
     */
    @ExcelProperty(value = "加工费号",index = 2)
    @ApiModelProperty(value = "加工费号")
    private String rawMaterialCode;

    /**
     * 加工费号描述
     */
    @ExcelProperty(value = "加工费号描述",index = 3)
    @ApiModelProperty(value = "加工费号描述")
    private String rawMaterialDesc;

    /**
     * 委外方式
     */
    @ExcelProperty(value = "委外方式",index = 4,converter = OutSourceTypeConverter.class)
    @ApiModelProperty(value = "委外方式")
    private String outsourceWay;

    /**
     * 创建者
     */
    @ExcelProperty(value = "创建者",index = 6)
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间",index = 5)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @ExcelProperty(value = "更新者",index = 8)
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "更新时间",index = 7)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 删除标志
     */
    private String delFlag;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注",index = 9)
    private String remark;

}
