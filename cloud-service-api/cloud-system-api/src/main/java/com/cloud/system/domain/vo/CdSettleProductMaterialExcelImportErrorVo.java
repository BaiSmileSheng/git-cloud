package com.cloud.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;


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
public class CdSettleProductMaterialExcelImportErrorVo {
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
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 加工费号
     */
    @ExcelProperty(value = "加工费号",index = 1)
    @ApiModelProperty(value = "加工费号")
    private String rawMaterialCode;

    /**
     * 加工费号描述
     */
    @ApiModelProperty(value = "加工费号描述")
    private String rawMaterialDesc;

    /**
     * 委外方式
     */
    @ExcelProperty(value = "加工承揽方式",index = 2)
    @ApiModelProperty(value = "加工承揽方式")
    private String outsourceWay;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注",index = 3)
    private String remark;
    /**
     * 错误信息
     */
    @ExcelProperty(value = "错误信息",index = 4)
    @ApiModelProperty(value = "错误信息")
    private String errorMessage;
    /**
     * 删除标志
     */
    private String delFlag;

}
