package com.cloud.order.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排产订单 对象 oms_production_order
 *
 * @author cs
 * @date 2020-05-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ExcelIgnoreUnannotated
public class OmsProductionOrderExportVoTest {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "生产订单号")
    private String productCode;

    @ExcelProperty(value = "交货量")
    private String productNum;

    @ExcelProperty(value = "实际结束日期")
    private String actEndDate;
}
