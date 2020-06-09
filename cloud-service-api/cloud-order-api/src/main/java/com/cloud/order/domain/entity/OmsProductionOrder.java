package com.cloud.order.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.order.converter.OutSourceTypeConverter;
import com.cloud.order.converter.ProductionOrderStatusConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
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
@ApiModel(value = "排产订单 ")
public class OmsProductionOrder extends BaseEntity {
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
    @ApiModelProperty(value = "排产订单号")
    private String orderCode;

    /**
     * 生产产订单号
     */
    @ExcelProperty(value = "生产产订单号",index = 0)
    @ApiModelProperty(value = "生产产订单号")
    private String productOrderCode;

    /**
     * 订单来源 1：内单，2：外单
     */
    @ApiModelProperty(value = "订单来源 1：内单，2：外单")
    private String orderFrom;

    /**
     * 订单种类 1：正常，2：追加，3：储备，4：新品，5：返修
     */
    @ApiModelProperty(value = "订单种类 1：正常，2：追加，3：储备，4：新品，5：返修")
    private String orderClass;

    /**
     * 分公司
     */
    @ApiModelProperty(value = "分公司")
    private String branchOffice;

    /**
     * 班长
     */
    @ApiModelProperty(value = "班长")
    private String monitor;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "专用号",index = 1)
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "专用号描述",index = 2)
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 工厂编码
     */
    @ExcelProperty(value = "工厂",index = 4)
    @ApiModelProperty(value = "工厂编码")
    private String factoryCode;

    /**
     * 工厂描述
     */
    @ApiModelProperty(value = "工厂描述")
    private String factoryDesc;

    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    /**
     * 排产量
     */
    @ExcelProperty(value = "订单数量",index = 3)
    @ApiModelProperty(value = "排产量")
    private BigDecimal productNum;

    /**
     * 交货量
     */
    @ApiModelProperty(value = "交货量")
    private BigDecimal deliveryNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 6)
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 线体号
     */
    @ExcelProperty(value = "线体号",index = 5)
    @ApiModelProperty(value = "线体号")
    private String productLineCode;

    /**
     * 生产开始日期
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生产开始日期")
    private Date productStartDate;

    /**
     * 生产结束日期
     */
    @ExcelProperty(value = "生产结束日期",index=9)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生产结束日期")
    private Date productEndDate;

    /**
     * 生产结束时间
     */
    @ExcelProperty(value = "生产结束时间",index=10)
    @ApiModelProperty(value = "生产结束时间")
    private String productEndTime;

    /**
     * 实际结束日期
     */
    @ExcelProperty(value = "实际结束日期",index=11)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "实际结束日期")
    private Date actualEndDate;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "BOM版本",index = 8)
    @ApiModelProperty(value = "BOM版本")
    private Long bomVersion;

    /**
     * 顺序
     */
    @ApiModelProperty(value = "顺序")
    private String sequence;

    /**
     * 发往地
     */
    @ApiModelProperty(value = "发往地")
    private String destination;

    /**
     * 交货日期
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "交货日期")
    private Date deliveryDate;

    /**
     * 老品/新品
     */
    @ApiModelProperty(value = "老品/新品")
    private String oldNew;

    /**
     * 用时
     */
    @ApiModelProperty(value = "用时")
    private BigDecimal useTime;

    /**
     * 节拍
     */
    @ApiModelProperty(value = "节拍")
    private String rhythm;

    /**
     * 产品定员
     */
    @ApiModelProperty(value = "产品定员")
    private Long productQuota;

    /**
     * PCB专用号
     */
    @ApiModelProperty(value = "PCB专用号")
    private String pcbSpecialCode;

    /**
     * 加工费
     */
    @ApiModelProperty(value = "加工费")
    private BigDecimal processCost;

    /**
     * 委外方式 0：半委外，1：全委外
     */
    @ExcelProperty(value = "委外方式" ,index = 7,converter = OutSourceTypeConverter.class)
    @ApiModelProperty(value = "委外方式 0：半委外，1：全委外")
    private String outsourceType;

    /**
     * 是否卡萨帝 0：否，1：是
     */
    @ApiModelProperty(value = "是否卡萨帝 0：否，1：是")
    private String csdFlag;

    /**
     * 产品状态
     */
    @ApiModelProperty(value = "产品状态")
    private String productStatus;

    /**
     * 生命周期
     */
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;

    /**
     * 状态 0：待评审，1：反馈中，2：待调整，3：已调整，4：待传SAP，5：已传SAP，6：已关单
     */
    @ExcelProperty(value = "状态 ",index=12,converter = ProductionOrderStatusConverter.class)
    @ApiModelProperty(value = "状态 0：待评审，1：反馈中，2：待调整，3：已调整，4：待传SAP，5：已传SAP，6：已关单")
    private String status;

    /**
     * 审核状态 0：待审核，1：JIT处长已审核
     */
    @ApiModelProperty(value = "审核状态 0：待审核，1：JIT处长已审核")
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

}
