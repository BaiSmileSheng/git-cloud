package com.cloud.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.system.domain.entity.CdBomInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * bom清单数据外部系统调用 对象 cd_bom
 *
 * @author lihongxia
 * @date 2020-09-14
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "bom清单数据 ")
public class CdBomInfoOtherSysVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 调用系统编码
     */
    @ApiModelProperty(value = "appId")
    private String appId;

    /**
     * 接口名称
     */
    @ApiModelProperty(value = "接口名称")
    private String interfaceName;

    /**
     * 成品物料号
     */
    @NotBlank(message = "成品物料号不能为空")
    @ApiModelProperty(value = "成品物料号")
    private String productMaterialCode;

    /**
     * 成品物料描述
     */
    @ApiModelProperty(value = "成品物料描述")
    private String productMaterialDesc;

    /**
     * 生产工厂编码
     */
    @NotBlank(message = "生产工厂编码不能为空")
    @ApiModelProperty(value = "生产工厂编码")
    private String productFactoryCode;

    /**
     * 版本
     */
    @ApiModelProperty(value = "版本")
    private String version;

    /**
     * pbom标记
     */
    @NotBlank(message = "pbom标记不能为空,0:新增或修改;1:删除")
    @ApiModelProperty(value = "pbom标记",notes = "0:新增或修改;1:删除")
    private String pbomFlag;

    /**
     * bom详情
     */
    @ApiModelProperty(value = "bom详情")
    private List<CdBomInfo> bomDetailList;
}
