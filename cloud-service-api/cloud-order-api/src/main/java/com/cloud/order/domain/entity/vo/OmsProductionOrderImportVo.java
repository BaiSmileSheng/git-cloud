package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.order.converter.OutSourceTypeConverter;
import com.cloud.order.converter.ProductionOrderClassConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 排产订单导入模板 OmsProductionOrderImportVo
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
public class OmsProductionOrderImportVo {
    private static final long serialVersionUID = 1L;
    /**
     * 订单类型
     */
    @ExcelProperty(value = "SAP订单类型", index = 0)
    @ApiModelProperty(value = "SAP订单类型")
    @NotBlank(message = "不能为空")
    private String orderType;
    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品专用号", index = 1)
    @ApiModelProperty(value = "成品专用号")
    @NotBlank(message = "不能为空")
    private String productMaterialCode;
    /**
     * 专用号描述
     */
    @ApiModelProperty(value = "专用号描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂", index = 2)
    @ApiModelProperty(value = "生产工厂")
    @NotBlank(message = "不能为空")
    private String productFactoryCode;
    /**
     * 线体号
     */
    @ExcelProperty(value = "产线号", index = 3)
    @ApiModelProperty(value = "产线号")
    @NotBlank(message = "不能为空")
    private String productLineCode;
    /**
     * 排产量
     */
    @ExcelProperty(value = "排产量", index = 4)
    @ApiModelProperty(value = "排产量")
    @NotNull(message = "不能为空")
    private BigDecimal productNum;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位", index = 5)
    @ApiModelProperty(value = "单位")
    @NotBlank(message = "不能为空")
    private String unit;
    /**
     * BOM版本
     */
    @ExcelProperty(value = "BOM版本", index = 6)
    @ApiModelProperty(value = "BOM版本")
    @NotBlank(message = "不能为空")
    private String bomVersion;


    /**
     * 生产开始日期
     */
    @ExcelProperty(value = "生产开始日期", index = 7)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "生产开始日期")
    @NotBlank(message = "不能为空")
    private String productStartDate;

    /**
     * 生产结束日期
     */
    @ExcelProperty(value = "生产结束日期", index = 8)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "生产结束日期")
    @NotBlank(message = "不能为空")
    private String productEndDate;

    /**
     * 生产结束时间
     */
    @ExcelProperty(value = "生产结束时间", index = 9)
    @ApiModelProperty(value = "生产结束时间")
    @NotBlank(message = "不能为空")
    private String productEndTime;

    /**
     * 交货日期
     */
    @ExcelProperty(value = "事业部T-1交货日期", index = 10)
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "事业部T-1交货日期")
    private String deliveryDate;

    /**
     * 顺序
     */
    @ExcelProperty(value = "顺序/备件", index = 11)
    @ApiModelProperty(value = "顺序/备件")
    private String sequence;

    /**
     * 发往地
     */
    @ExcelProperty(value = "发往地", index = 12)
    @ApiModelProperty(value = "发往地")
    private String destination;

    /**
     * 产品状态
     */
    @ExcelProperty(value = "产品状态", index = 13)
    @ApiModelProperty(value = "产品状态")
    private String productStatus;

    /**
     * 老品/新品
     */
    @ExcelProperty(value = "老品/新品", index = 14)
    @ApiModelProperty(value = "老品/新品")
    private String oldNew;

    /**
     * PCB专用号
     */
    @ExcelProperty(value = "PCB专用号", index = 15)
    @ApiModelProperty(value = "PCB专用号")
    private String pcbSpecialCode;
    /**
     * 是否卡萨帝 0：否，1：是
     */
    @ExcelProperty(value = "是否卡萨帝", index = 16)
    @ApiModelProperty(value = "是否卡萨帝 0：否，1：是")
    private String csdFlag;
    /**
     * 加工承揽方式 0：半成品，1：成品，2：自制
     */
    @ExcelProperty(value = "加工承揽方式", index = 17, converter = OutSourceTypeConverter.class)
    @ApiModelProperty(value = "加工承揽方式 0：半成品，1：成品，2：自制")
    @NotBlank(message = "不能为空")
    private String outsourceType;

    /**
     * 订单种类 1：正常，2：追加，3：储备，4：新品，5：返修
     */
    @ExcelProperty(value = "订单分类", index = 18,converter= ProductionOrderClassConverter.class)
    @ApiModelProperty(value = "订单种类 1：正常，2：追加，3：储备，4：新品，5：返修")
    @NotBlank(message = "不能为空")
    private String orderClass;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 19)
    private String remark;

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
     * 用时
     */
    @ApiModelProperty(value = "用时")
    private BigDecimal useTime;

    /**
     * 节拍
     */
    @ApiModelProperty(value = "节拍")
    private BigDecimal rhythm;

    /**
     * 产品定员
     */
    @ApiModelProperty(value = "产品定员")
    private int productQuota;

    /**
     * 生命周期
     */
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;


    /**
     * 状态 0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单
     */
    @ApiModelProperty(value = "状态 0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单")
    private String status;

    /**
     * 审核状态 0：待审核，1：JIT处长已审核
     */
    @ApiModelProperty(value = "审核状态 0：无需审核，1：审核中，2：审核完成")
    private String auditStatus;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;


}
