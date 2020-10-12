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
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * 外单排产差异报表 对象 oms_product_difference
 *
 * @author ltq
 * @date 2020-09-30
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "外单排产差异报表 ")
public class OmsProductDifference extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 工厂
     */
    @ExcelProperty(value = "工厂",index = 0)
    @ApiModelProperty(value = "工厂")
    private String productFactoryCode;

    /**
     * 产品类别 1:空调内板,2:空调外板,3:空调模块,4:空调显示,5:波轮显示,6:波轮电源驱动,7:滚筒显示,8:滚筒电源驱动,9:冰箱显示,10:冰箱主控,11:电热显示,12:电热主控,13:厨电显示,14:厨电主控,15:洗碗机,16:液晶模组,17:云单（购销业务）,18:海达维B2C,19:海达维照明模块,20:海达维杀菌模块,21:海达维购销物料,22:HMI,23:物联网模块,24:半成品
     */
    @ExcelProperty(value = "产品类别",index = 1)
    @ApiModelProperty(value = "产品类别")
    private String productType;

    /**
     * 专用号
     */
    @ExcelProperty(value = "专用号",index = 2)
    @ApiModelProperty(value = "专用号")
    private String productMaterialCode;

    /**
     * 周数
     */
    @ExcelProperty(value = "周数",index = 3)
    @ApiModelProperty(value = "周数")
    private String weeks;

    /**
     * 应排T+1周订单
     */
    @ExcelProperty(value = "应排T+1周订单",index = 4)
    @ApiModelProperty(value = "应排T+1周订单")
    private BigDecimal realOrderNum;

    /**
     * 实际排产数
     */
    @ExcelProperty(value = "实际排产数",index = 5)
    @ApiModelProperty(value = "实际排产数")
    private BigDecimal productNum;

    /**
     * 差异量
     */
    @ExcelProperty(value = "差异量",index = 6)
    @ApiModelProperty(value = "差异量")
    private BigDecimal differenceNum;

    /**
     * 排产率
     */
    @ExcelProperty(value = "排产率",index = 7)
    @ApiModelProperty(value = "排产率")
    private String productivity;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
