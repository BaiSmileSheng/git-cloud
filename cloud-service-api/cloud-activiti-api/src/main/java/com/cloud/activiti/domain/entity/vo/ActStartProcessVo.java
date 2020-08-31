package com.cloud.activiti.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "审批流开启VO")
public class ActStartProcessVo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务ID")
    private String orderId;
    @ApiModelProperty(value = "业务Code")
    private String orderCode;
    @ApiModelProperty(value = "审批人Set")
    private Set<String> userIds;
}
