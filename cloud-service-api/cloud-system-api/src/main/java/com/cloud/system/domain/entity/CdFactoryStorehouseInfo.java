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
 * 工厂库位 对象 cd_factory_storehouse_info
 *
 * @author cs
 * @date 2020-06-15
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "工厂库位 ")
public class CdFactoryStorehouseInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 生产工厂编码
     */
    @ExcelProperty(value = "生产工厂",index = 0)
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
    @ExcelProperty(value = "客户编码",index = 1)
    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    /**
     * 客户描述
     */
    @ApiModelProperty(value = "客户描述")
    private String customerDesc;

    /**
     * 发货库位
     */
    @ExcelProperty(value = "发货库位",index = 2)
    @ApiModelProperty(value = "发货库位")
    private String storehouseFrom;

    /**
     * 接收库位
     */
    @ExcelProperty(value = "接收库位",index = 3)
    @ApiModelProperty(value = "接收库位")
    private String storehouseTo;

    /**
     * 提前量
     */
    @ExcelProperty(value = "提前量(天)",index = 4)
    @ApiModelProperty(value = "提前量")
    private String leadTime;

    /**
     * 是否删除0：有效，1：删除
     */
    private String delFlag;

}
