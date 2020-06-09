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
import java.math.BigDecimal;

/**
 * 汇率对象 cd_mouth_rate
 *
 * @author cs
 * @date 2020-05-27
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = " 汇率")
public class CdMouthRate extends BaseEntity {
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
     * 汇率
     */
    @ExcelProperty(value = "汇率")
    @ApiModelProperty(value = "汇率")
    private BigDecimal rate;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

}
