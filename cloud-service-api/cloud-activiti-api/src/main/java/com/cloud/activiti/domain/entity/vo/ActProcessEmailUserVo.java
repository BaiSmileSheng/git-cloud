package com.cloud.activiti.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "审批邮件通知Vo")
public class ActProcessEmailUserVo {
    @ApiModelProperty(value = "邮件地址")
    private String email;
    @ApiModelProperty(value = "邮件内容")
    private String context;
    @ApiModelProperty(value = "邮件标题")
    private String title;
}
