package com.cloud.system.domain.entity;

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


/**
 * 报废每月单号对象 cd_scrap_month_no
 *
 * @author cs
 * @date 2020-09-25
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "报废每月单号")
public class CdScrapMonthNo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 年月
     */
    @ExcelProperty(value = "年月")
    @ApiModelProperty(value = "年月")
    private String yearMouth;

    /**
     * 工厂编码
     */
    @ExcelProperty(value = "工厂编码")
    @ApiModelProperty(value = "工厂编码")
    private String factoryCode;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

    private String remark;

}
