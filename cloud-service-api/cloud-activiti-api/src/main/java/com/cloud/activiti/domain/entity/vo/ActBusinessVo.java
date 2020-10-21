package com.cloud.activiti.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "审批流Vo")
public class ActBusinessVo {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "审批流Key")
    private String key;
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "审批流标题")
    private String title;
    @ApiModelProperty(value = "审批流业务数据List")
    private List<ActStartProcessVo> processVoList;
    @ApiModelProperty(value = "审批通知邮件List")
    private List<ActProcessEmailUserVo> processEmailUserVoList;
}
