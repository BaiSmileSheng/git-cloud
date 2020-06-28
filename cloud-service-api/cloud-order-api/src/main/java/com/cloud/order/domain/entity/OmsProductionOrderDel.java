package com.cloud.order.domain.entity;

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

import tk.mybatis.mapper.annotation.KeySql;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 排产订单删除 对象 oms_production_order_del
 *
 * @author ltq
 * @date 2020-06-22
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "排产订单删除 ")
public class OmsProductionOrderDel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 排产订单号
     */
    @ExcelProperty(value = "排产订单号")
    @ApiModelProperty(value = "排产订单号")
    private String orderCode;

    /**
     * 生产订单号
     */
    @ExcelProperty(value = "生产订单号")
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 订单种类 1：正常，2：追加，3：储备，4：新品，5：返修
     */
    @ExcelProperty(value = "订单种类 1：正常，2：追加，3：储备，4：新品，5：返修")
    @ApiModelProperty(value = "订单种类 1：正常，2：追加，3：储备，4：新品，5：返修")
    private String orderClass;

    /**
     * 分公司
     */
    @ExcelProperty(value = "分公司")
    @ApiModelProperty(value = "分公司")
    private String branchOffice;

    /**
     * 班长
     */
    @ExcelProperty(value = "班长")
    @ApiModelProperty(value = "班长")
    private String monitor;

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
     * 订单类型
     */
    @ExcelProperty(value = "订单类型")
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    /**
     * 排产量
     */
    @ExcelProperty(value = "排产量")
    @ApiModelProperty(value = "排产量")
    private BigDecimal productNum;

    /**
     * 交货量
     */
    @ExcelProperty(value = "交货量")
    @ApiModelProperty(value = "交货量")
    private BigDecimal deliveryNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 线体号
     */
    @ExcelProperty(value = "线体号")
    @ApiModelProperty(value = "线体号")
    private String productLineCode;

    /**
     * 生产开始日期
     */
    @ExcelProperty(value = "生产开始日期")
    @ApiModelProperty(value = "生产开始日期")
    private String productStartDate;

    /**
     * 生产结束日期
     */
    @ExcelProperty(value = "生产结束日期")
    @ApiModelProperty(value = "生产结束日期")
    private String productEndDate;

    /**
     * 生产结束时间
     */
    @ExcelProperty(value = "生产结束时间")
    @ApiModelProperty(value = "生产结束时间")
    private String productEndTime;

    /**
     * 实际结束日期
     */
    @ExcelProperty(value = "实际结束日期")
    @ApiModelProperty(value = "实际结束日期")
    private Date actualEndDate;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "BOM版本")
    @ApiModelProperty(value = "BOM版本")
    private String bomVersion;

    /**
     * 新BOM版本
     */
    @ExcelProperty(value = "新BOM版本")
    @ApiModelProperty(value = "新BOM版本")
    private String newVersion;

    /**
     * 顺序
     */
    @ExcelProperty(value = "顺序")
    @ApiModelProperty(value = "顺序")
    private String sequence;

    /**
     * 发往地
     */
    @ExcelProperty(value = "发往地")
    @ApiModelProperty(value = "发往地")
    private String destination;

    /**
     * 交货日期
     */
    @ExcelProperty(value = "交货日期")
    @ApiModelProperty(value = "交货日期")
    private String deliveryDate;

    /**
     * 老品/新品
     */
    @ExcelProperty(value = "老品/新品")
    @ApiModelProperty(value = "老品/新品")
    private String oldNew;

    /**
     * 用时
     */
    @ExcelProperty(value = "用时")
    @ApiModelProperty(value = "用时")
    private BigDecimal useTime;

    /**
     * 节拍
     */
    @ExcelProperty(value = "节拍")
    @ApiModelProperty(value = "节拍")
    private BigDecimal rhythm;

    /**
     * 产品定员
     */
    @ExcelProperty(value = "产品定员")
    @ApiModelProperty(value = "产品定员")
    private Integer productQuota;

    /**
     * PCB专用号
     */
    @ExcelProperty(value = "PCB专用号")
    @ApiModelProperty(value = "PCB专用号")
    private String pcbSpecialCode;

    /**
     * 加工费
     */
    @ExcelProperty(value = "加工费")
    @ApiModelProperty(value = "加工费")
    private BigDecimal processCost;

    /**
     * 加工承揽方式 0：半成品，1：成品
     */
    @ExcelProperty(value = "加工承揽方式 0：半成品，1：成品")
    @ApiModelProperty(value = "加工承揽方式 0：半成品，1：成品")
    private String outsourceType;

    /**
     * 是否卡萨帝 0：否，1：是
     */
    @ExcelProperty(value = "是否卡萨帝 0：否，1：是")
    @ApiModelProperty(value = "是否卡萨帝 0：否，1：是")
    private String csdFlag;

    /**
     * 产品状态
     */
    @ExcelProperty(value = "产品状态")
    @ApiModelProperty(value = "产品状态")
    private String productStatus;

    /**
     * 生命周期
     */
    @ExcelProperty(value = "生命周期")
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;

    /**
     * 状态 0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单
     */
    @ExcelProperty(value = "状态 0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单")
    @ApiModelProperty(value = "状态 0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单")
    private String status;

    /**
     * 审核状态 0：无需审核，1：审核中，2：审核完成
     */
    @ExcelProperty(value = "审核状态 0：无需审核，1：审核中，2：审核完成")
    @ApiModelProperty(value = "审核状态 0：无需审核，1：审核中，2：审核完成")
    private String auditStatus;

    /**
     * SAP返回信息 传SAP返回信息
     */
    @ExcelProperty(value = "SAP返回信息 传SAP返回信息")
    @ApiModelProperty(value = "SAP返回信息 传SAP返回信息")
    private String sapMessages;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

}
