package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 排产订单 对象 oms_production_order
 *
 * @author cs
 * @date 2020-05-29
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "排产订单邮件推送")
public class OmsProductionOrderMailVo extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 排产订单号
     */
    @ApiModelProperty(value = "排产订单号")
    private String orderCode;

    /**
     * 生产产订单号
     */
    @ExcelProperty(value = "订单批次号",index = 3)
    @ApiModelProperty(value = "生产订单号")
    private String productOrderCode;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型")
    @NotBlank
    private String orderType;

    /**
     * 订单种类 1：正常，2：追加，3：储备，4：新品，5：返修
     */
    @ApiModelProperty(value = "订单种类 1：正常，2：追加，3：储备，4：新品，5：返修")
    private String orderClass;

    /**
     * 分公司
     */
    @ExcelProperty(value = "分公司",index = 0)
    @ApiModelProperty(value = "分公司")
    private String branchOffice;

    /**
     * 班长
     */
    @ExcelProperty(value = "班长",index = 1)
    @ApiModelProperty(value = "班长")
    private String monitor;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品专用号",index = 4)
    @ApiModelProperty(value = "成品物料号")
    @NotBlank
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品描述",index = 5)
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ApiModelProperty(value = "生产工厂编码")
    @NotBlank
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 线体号
     */
    @ExcelProperty(value = "线号",index = 2)
    @ApiModelProperty(value = "线体号")
    @NotBlank
    private String productLineCode;

    /**
     * 排产量
     */
    @ExcelProperty(value = "排产订单数量",index =7)
    @ApiModelProperty(value = "排产量")
    @NotBlank
    private BigDecimal productNum;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    @NotBlank
    private String unit;

    /**
     * 基本开始日期
     */
    @ExcelProperty(value = "基本开始日期",index = 8)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "基本开始日期")
    @NotBlank
    private String productStartDate;

    /**
     * 基本结束日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "基本结束日期")
    @NotBlank
    private String productEndDate;

    /**
     * 基本结束时间
     */
    @ApiModelProperty(value = "基本结束时间")
    @NotBlank
    private String productEndTime;
    /**
     * 交货日期
     */
    @ExcelProperty(value = "事业部T-1交货",index=10)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "交货日期")
    @NotBlank
    private String deliveryDate;

    /**
     * 实际结束日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "实际结束日期")
    private Date actualEndDate;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "版本",index = 14)
    @ApiModelProperty(value = "BOM版本")
    @NotBlank
    private String bomVersion;

    /**
     * 新BOM版本
     */
    @ApiModelProperty(value = "新BOM版本")
    private String newVersion;

    /**
     * 老品/新品
     */
    @ExcelProperty(value = "老品/新品",index = 16)
    @ApiModelProperty(value = "老品/新品")
    private String oldNew;

    /**
     * 用时
     */
    @ExcelProperty(value = "产品用时",index =12 )
    @ApiModelProperty(value = "用时")
    private BigDecimal useTime;

    /**
     * 节拍
     */
    @ExcelProperty(value = "UPH",index = 11)
    @ApiModelProperty(value = "节拍")
    private BigDecimal rhythm;

    /**
     * 产品定员
     */
    @ExcelProperty(value = "产品定员",index =13 )
    @ApiModelProperty(value = "产品定员")
    private Integer productQuota;

    /**
     * PCB专用号
     */
    @ExcelProperty(value = "PCB专用号",index = 6)
    @ApiModelProperty(value = "PCB专用号")
    @NotBlank
    private String pcbSpecialCode;
    /**
     * 委外方式 0：半委外，1：全委外，2：自制
     */
    @ApiModelProperty(value = "委外方式 0：半委外，1：全委外，2：自制")
    @NotBlank
    private String outsourceType;
    /**
     * 状态 0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单
     */
    @ApiModelProperty(value = "状态 0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单")
    private String status;
    /**
     * sap返回信息
     */
    @ApiModelProperty(value = "sap返回信息")
    private String sapMessages;

    /**
     * 交货量
     */
    @ApiModelProperty(value = "交货量")
    private BigDecimal deliveryNum;


    /**
     * 顺序
     */
    @ExcelProperty(value = "顺序",index = 9)
    @ApiModelProperty(value = "顺序")
    private String sequence;

    /**
     * 发往地
     */
    @ExcelProperty(value = "顺序",index = 15)
    @ApiModelProperty(value = "发往地")
    private String destination;

    /**
     * 加工费
     */
    @ApiModelProperty(value = "加工费")
    private BigDecimal processCost;



    /**
     * 是否卡萨帝 0：否，1：是
     */
    @ExcelProperty(value = "是否卡萨帝",index = 18)
    @ApiModelProperty(value = "是否卡萨帝 0：否，1：是")
    private String csdFlag;

    /**
     * 产品状态
     */
    @ExcelProperty(value = "产品状态",index = 17)
    @ApiModelProperty(value = "产品状态")
    private String productStatus;

    /**
     * 生命周期
     */
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;

    /**
     * 审核状态 0：待审核，1：JIT处长已审核
     */
    @ApiModelProperty(value = "审核状态 0：无需审核，1：审核中，2：审核完成")
    private String auditStatus;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 查询生产结束日期起始值
     */
    @Transient
    @ApiModelProperty(value = "查询生产结束日期起始值")
    private String productEndDateStart;

    /**
     * 查询生产结束日期结束
     */
    @Transient
    @ApiModelProperty(value = "查询生产结束日期结束值")
    private String productEndDateEnd;

    /**
     * 实际结束日期起始值
     */
    @Transient
    @ApiModelProperty(value = "查询实际结束日期起始值")
    private String actualEndDateStart;

    /**
     * 实际结束日期结束值
     */
    @Transient
    @ApiModelProperty(value = "查询实际结束日期结束值")
    private String actualEndDateEnd;
    /**
     * 查询日期类型，1：交付日期，2：生产开始日期，3：生产结束日期
     */
    @Transient
    @ApiModelProperty(value = "查询日期类型")
    private String dateType;
    /**
     * 生产工厂权限
     */
    @Transient
    @ApiModelProperty(value = "生产工厂权限")
    private String productFactoryQuery;
    /**
     * 排产订单id字符串
     */
    @Transient
    @ApiModelProperty(value = "排产订单id字符串")
    private String ids;

    /**
     * 查询日期起始值
     */
    @Transient
    @ApiModelProperty(value = "查询日期起始值")
    private String checkDateStart;

    /**
     * 查询日期结束值
     */
    @Transient
    @ApiModelProperty(value = "查询日期结束值")
    private String checkDateEnd;

    /**
     * SAP标记
     */
    @Transient
    private String sapFlag;

    /**
     * 流程实例ID
     */
    @Transient
    private String procDefId;

    /**
     * 流程实例名称
     */
    @Transient
    private String procName;
}
