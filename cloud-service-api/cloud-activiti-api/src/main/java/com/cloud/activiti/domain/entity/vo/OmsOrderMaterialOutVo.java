package com.cloud.activiti.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * 下市开启审批流vo
 *
 * @author lihongxia
 * @date 2020-06-23
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "下市开启审批流vo")
public class OmsOrderMaterialOutVo {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "编号")
    private String orderCode;

    @ApiModelProperty(value = "登录人id")
    private Long loginId;

    @ApiModelProperty(value = "创建人名称")
    private String createBy;

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "工厂编号")
    private String factoryCode;

    @ApiModelProperty(value = "下市开启审批流vo集合")
    private List<OmsOrderMaterialOutVo> omsOrderMaterialOutVoList;

    /**
     * 流程实例ID
     */
    @Transient
    private String procDefId;

    /**
     * 流程实例名称
     */
    @Transient
    private String procName;

}
