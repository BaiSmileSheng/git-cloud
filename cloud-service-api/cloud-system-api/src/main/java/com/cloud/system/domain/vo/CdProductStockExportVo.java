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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 成品库存主 对象 cd_product_stock
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "成品库存导出信息")
public class CdProductStockExportVo{
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料号",index = 0)
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码",index = 1)
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 库存总量
     */
    @ExcelProperty(value = "总库存",index = 2)
    @ApiModelProperty(value = "库存总量")
    private BigDecimal sumNum;

    /**
     * 寄售不足
     */
    @ExcelProperty(value = "寄售不足",index = 3)
    @ApiModelProperty(value = "寄售不足")
    private BigDecimal stockKNum;

    /**
     * 库位
     */
    @ExcelProperty(value = {"在库","库位"},index = 4)
    @ApiModelProperty(value = "库位")
    private String storehouse;
    /**
     * 在库库存
     */
    @ExcelProperty(value = {"在库","在库量"},index = 5)
    @ApiModelProperty(value = "在库库存")
    private BigDecimal warehouseNum;

    /**
     * 发出库位
     */
    @ExcelProperty(value = {"在途","发出库位"},index = 6)
    @ApiModelProperty(value = "发出库位")
    private String storehouseFrom;

    /**
     * 接收库位
     */
    @ExcelProperty(value = {"在途","接收库位"},index = 7)
    @ApiModelProperty(value = "接收库位")
    private String storehouseTo;

    /**
     * 在途量
     */
    @ExcelProperty(value = {"在途","在途量"},index = 8)
    @ApiModelProperty(value = "在途量")
    private BigDecimal passageNum;

    /**
     * 在产版本
     */
    @ExcelProperty(value = {"在产","版本"},index = 9)
    @ApiModelProperty(value = "在产版本")
    private String inProductionVersion;

    /**
     * 在产量
     */
    @ExcelProperty(value = {"在产","在产量"},index = 10)
    @ApiModelProperty(value = "在产量")
    private BigDecimal inProductionNum;

    /**
     * 不良库位
     */
    @ExcelProperty(value = {"不良","库位"},index = 11)
    @ApiModelProperty(value = "库位")
    private String storehouseB;
    /**
     * 不良在库库存
     */
    @ExcelProperty(value = {"不良","不良量"},index = 12)
    @ApiModelProperty(value = "在库库存")
    private BigDecimal warehouseNumB;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间",index = -1)
    private Date createTime;

}
