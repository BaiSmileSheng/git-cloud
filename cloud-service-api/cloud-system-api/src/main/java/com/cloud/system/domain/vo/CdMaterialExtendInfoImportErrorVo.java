package com.cloud.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.system.converter.GetStockConverter;
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
 * 导入错误 物料扩展信息 对象 cd_material_extend_info
 *
 * @author lihongia
 * @date 2020-06-15
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "物料扩展信息 ")
public class CdMaterialExtendInfoImportErrorVo{
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 物料号
     */
    @ExcelProperty(value = "成品专用号",index = 0)
    @ApiModelProperty(value = "物料号")
    private String materialCode;

    /**
     * 物料描述
     */
    @ApiModelProperty(value = "物料描述")
    private String materialDesc;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 产品类别
     */
    @ExcelProperty(value = "产品类别",index = 1)
    @ApiModelProperty(value = "产品类别")
    private String productType;

    /**
     * 生命周期
     */
    @ExcelProperty(value = "生命周期",index = 2)
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;

    /**
     * 可否加工承揽
     */
    @ApiModelProperty(value = "可否加工承揽")
    private String isPuttingOut;

    /**
     * 是否ZN认证
     */
    @ExcelProperty(value = "是否ZN认证",index = 3)
    @ApiModelProperty(value = "是否ZN认证")
    private String isZnAttestation;

    /**
     * 获取库存
     */
    @ExcelProperty(value = "获取库存",index = 4,converter = GetStockConverter.class)
    @ApiModelProperty(value = "获取库存")
    private String isGetStock;

    /**
     * 建立日期
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "建立日期")
    private Date establishDate;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

    /**
     * 错误信息
     */
    @ExcelProperty(value = "错误信息",index = 5)
    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

}
