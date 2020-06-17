package com.cloud.order.domain.entity.vo;

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
import java.math.BigDecimal;

/**
 * 真单对象 oms_real_order
 *
 * @author ltq
 * @date 2020-06-15
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "真单VO")
public class OmsRealOrderVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料号")
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品物料描述")
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码")
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户描述")
    @ApiModelProperty(value = "客户描述")
    private String customerDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂编码")
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ExcelProperty(value = "生产工厂描述")
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * MRP范围
     */
    @ExcelProperty(value = "MRP范围")
    @ApiModelProperty(value = "MRP范围")
    private String mrpRange;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "BOM版本")
    @ApiModelProperty(value = "BOM版本")
    private String bomVersion;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组")
    @ApiModelProperty(value = "采购组")
    private String purchaseGroupCode;
    /**
     * 累计需求量
     */
    @ExcelProperty(value = "累计需求量")
    @ApiModelProperty(value = "累计需求量")
    private BigDecimal totalOrderNum;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "订单数量")
    @ApiModelProperty(value = "订单数量")
    private BigDecimal orderNum;
    /**
     * 库位
     */
    @ExcelProperty(value = "库位")
    @ApiModelProperty(value = "库位")
    private String storehouse;
    /**
     * 库位库存
     */
    @ExcelProperty(value = "库位库存")
    @ApiModelProperty(value = "库位库存")
    private BigDecimal storehouseNum;
    /**
     * 在途量
     */
    @ExcelProperty(value = "在途量")
    @ApiModelProperty(value = "在途量")
    private BigDecimal passageNum;
    /**
     * 缺口量
     */
    @ExcelProperty(value = "缺口量")
    @ApiModelProperty(value = "缺口量")
    private BigDecimal breachNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 交付日期
     */
    @ExcelProperty(value = "交付日期")
    @ApiModelProperty(value = "交付日期")
    private String deliveryDate;

    /**
     * 生产日期
     */
    @ExcelProperty(value = "生产日期")
    @ApiModelProperty(value = "生产日期")
    private String productDate;

    /**
     * 地点
     */
    @ExcelProperty(value = "地点")
    @ApiModelProperty(value = "地点")
    private String place;

    /**
     * 状态 0：初始，1：已调整
     */
    @ExcelProperty(value = "状态 0：初始，1：已调整")
    @ApiModelProperty(value = "状态 0：初始，1：已调整")
    private String status;

    /**
     * 数据源 0：接口接入，1：人工导入
     */
    @ExcelProperty(value = "数据源 0：接口接入，1：人工导入")
    @ApiModelProperty(value = "数据源 0：接口接入，1：人工导入")
    private String dataSource;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

}
