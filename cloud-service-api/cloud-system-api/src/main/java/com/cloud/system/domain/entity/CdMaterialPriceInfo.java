package com.cloud.system.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
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
import java.math.BigDecimal;
import java.util.Date;

/**
 * SAP成本价格 对象 cd_material_price_info
 *
 * @author cs
 * @date 2020-05-26
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "SAP成本价格 ")
public class CdMaterialPriceInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 物料编码
     */
    @ExcelProperty(value = "物料编码")
    @ApiModelProperty(value = "物料编码")
    private String materialCode;

    /**
     * 物料描述
     */
    @ExcelProperty(value = "物料描述")
    @ApiModelProperty(value = "物料描述")
    private String materialDesc;

    /**
     * 加工费价格
     */
    @ExcelProperty(value = "加工费价格")
    @ApiModelProperty(value = "加工费价格")
    private BigDecimal processPrice;

    /**
     * 供应商
     */
    @ExcelProperty(value = "供应商")
    @ApiModelProperty(value = "供应商")
    private String memberCode;

    /**
     * 供应商描述
     */
    @ExcelProperty(value = "供应商描述")
    @ApiModelProperty(value = "供应商描述")
    private String memberName;

    /**
     * 采购组
     */
    @ExcelProperty(value = "采购组织")
    @ApiModelProperty(value = "采购组织")
    private String purchasingGroup;

    /**
     * 税码
     */
    @ExcelProperty(value = "税码")
    @ApiModelProperty(value = "税码")
    private String taxCode;

    /**
     * 净价值=PB00+ZDLP
     */
    @ExcelProperty(value = "净价值=PB00+ZDLP")
    @ApiModelProperty(value = "净价值=PB00+ZDLP")
    private BigDecimal netWorth;

    /**
     * PB00
     */
    @ExcelProperty(value = "PB00")
    @ApiModelProperty(value = "PB00")
    private String kbetr;

    /**
     * 货币
     */
    @ExcelProperty(value = "货币")
    @ApiModelProperty(value = "货币")
    private String currency;

    /**
     * 价格单位
     */
    @ExcelProperty(value = "价格单位")
    @ApiModelProperty(value = "价格单位")
    private String priceUnit;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
     * 代理费-ZDLP
     */
    @ExcelProperty(value = "代理费-ZDLP")
    @ApiModelProperty(value = "代理费-ZDLP")
    private BigDecimal agencyFee;

    /**
     * 有效期开始日期
     */
    @ExcelProperty(value = "有效期开始日期")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期开始日期")
    private Date beginDate;

    /**
     * 有效期结束日期
     */
    @ExcelProperty(value = "有效期结束日期")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期结束日期")
    private Date endDate;

    /**
     * SAP创建日期
     */
    @ExcelProperty(value = "SAP创建日期")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "SAP创建日期")
    private Date sapCreatedDate;

    /**
     * 删除状态
     */
    private String delFlag;


    /**
     * 根据物料号和采购组织分组
     */
    @JsonIgnore //swagger 不显示字段
    @Transient  //tk 不操作字段
    private String mapKey;

}
