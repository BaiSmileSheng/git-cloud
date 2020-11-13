package com.cloud.order.domain.entity;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.order.converter.DemandOrderGatherEditAuditStatusConverter;
import com.cloud.order.converter.DemandOrderGatherEditStatusConverter;
import com.cloud.order.converter.OrderFromConverter;
import com.cloud.order.domain.entity.vo.DayAndNumsGatherVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * T+1-T+2周需求导入 对象 oms_2weeks_demand_order_edit
 *
 * @author cs
 * @date 2020-06-22
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "T+1-T+2周需求导入 ")
public class Oms2weeksDemandOrderEdit extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 需求订单号
     */
    @ExcelProperty(value = "订单号",index = 0)
    @ApiModelProperty(value = "需求订单号")
    private String demandOrderCode;

    /**
     * 订单类型
     */
    @ExcelProperty(value = "SAP订单类型",index = 1)
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    /**
     * 订单来源 1：内单，2：外单
     */
    @ExcelProperty(value = "订单来源",index = 12,converter = OrderFromConverter.class)
    @ApiModelProperty(value = "订单来源 1：内单，2：外单")
    private String orderFrom;

    /**
     * 成品物料号
     */
    @ExcelProperty(value = "成品物料",index = 2)
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ExcelProperty(value = "成品物料描述",index = 3)
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 产品类别
     */
    @ExcelProperty(value = "产品类别",index = 15)
    @ApiModelProperty(value = "产品类别")
    private String productType;

    /**
     * 生命周期
     */
    @ExcelProperty(value = "生命周期",index = 14)
    @ApiModelProperty(value = "生命周期")
    private String lifeCycle;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "工厂",index = 4)
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 生产工厂描述
     */
    @ApiModelProperty(value = "生产工厂描述")
    private String productFactoryDesc;

    /**
     * 客户编码
     */
    @ExcelProperty(value = "客户编码",index = 5)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户描述
     */
    @ExcelProperty(value = "客户名称",index = 6)
    @ApiModelProperty(value = "客户描述")
    private String customerDesc;

    /**
     * MRP范围
     */
    @ExcelProperty(value = "MRP范围",index = 7)
    @ApiModelProperty(value = "MRP范围")
    private String mrpRange;

    /**
     * BOM版本
     */
    @ExcelProperty(value = "版本",index = 8)
    @ApiModelProperty(value = "BOM版本")
    private String bomVersion;

    /**
     * 采购组
     */
    @ApiModelProperty(value = "采购组")
    private String purchaseGroupCode;

    /**
     * 地点
     */
    @ExcelProperty(value = "地点",index = 11)
    @ApiModelProperty(value = "地点")
    private String place;

    /**
     * 交付日期
     */
    @ExcelProperty(value = "交付日期",index = 10)
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "交付日期")
    private Date deliveryDate;

    /**
     * 年度 交货日期年度
     */
    @ApiModelProperty(value = "年度 交货日期年度")
    private String year;

    /**
     * 周数 交货日期周数
     */
    @ExcelProperty(value = "周数",index = 13)
    @ApiModelProperty(value = "周数 交货日期周数")
    private String weeks;

    /**
     * 订单数量
     */
    @ExcelProperty(value = "数量",index = 9)
    @ApiModelProperty(value = "订单数量")
    private Long orderNum;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 数据版本 年度-周数
     */
    @ApiModelProperty(value = "数据版本 年度-周数")
    private String version;

    /**
     * 计划订单号
     */
    @ApiModelProperty(value = "计划订单号")
    private String planOrderOrder;

    /**
     * SAP返回信息 传SAP返回信息
     */
    @ExcelProperty(value = "SAP返回信息",index = 18)
    @ApiModelProperty(value = "SAP返回信息 传SAP返回信息")
    private String sapMessages;

    /**
     * 状态 0：初始，1：待传SAP，2：传SAP中，3：已传SAP，4：传SAP异常
     */
    @ExcelProperty(value = "状态",index = 16,converter= DemandOrderGatherEditStatusConverter.class)
    @ApiModelProperty(value = "状态 0：初始，1：待传SAP，2：传SAP中，3：已传SAP，4：传SAP异常")
    private String status;

    /**
     * 审核状态 0：无需审核，1：审核中，2：审核完成
     */
    @ExcelProperty(value = "审核状态",index = 17,converter = DemandOrderGatherEditAuditStatusConverter.class)
    @ApiModelProperty(value = "审核状态 0：无需审核，1：审核中，2：审核完成")
    private String auditStatus;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

    /**
     * 工厂列表
     */
    @Transient
    List<String> productFactoryList;

    /**
     * 汇总展示数据
     */
    @Transient  //tk 不操作字段
    List<DayAndNumsGatherVO> dayDataList;

    @JsonIgnore
    public String getDeliveryDateStr() {
        return DateUtil.formatDate(this.deliveryDate);
    }

    /**
     * id逗号分隔
     */
    @Transient
    private String ids;
}
