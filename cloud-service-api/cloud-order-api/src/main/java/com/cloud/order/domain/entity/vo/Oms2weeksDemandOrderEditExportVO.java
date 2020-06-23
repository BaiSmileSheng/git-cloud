package com.cloud.order.domain.entity.vo;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * T+1-T+2周需求导入 导出类
 *
 * @author cs
 * @date 2020-06-22
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Oms2weeksDemandOrderEditExportVO  {
    private static final long serialVersionUID = 1L;


    /**
     * 成品物料号
     */
    @ExcelProperty(value = "专用号",index = 0)
    private String productMaterialCode;


    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂",index = 1)
    private String productFactoryCode;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 2)
    private String unit;
    /**
     * 交付日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    /**
     * 日期
     */
    @ExcelProperty(value = "日期",index = 6)
    private String day;

    /**
     * 接口接入量
     */
    @ExcelProperty(value = "接口接入量",index = 3)
    @ApiModelProperty(value = "接口接入量")
    private Long interfaceNum;

    /**
     * 人工导入量
     */
    @ExcelProperty(value = "人工导入量",index = 4)
    private Long artificialNum;

    /**
     * 差异量
     */
    @ExcelProperty(value = "差异量",index = 5)
    private Long differenceNum;

    @JsonIgnore
    public String getDeliveryDateStr() {
        return DateUtil.formatDate(this.deliveryDate);
    }
}
