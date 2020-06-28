package com.cloud.activiti.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.cloud.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程实例
 *
 * @author ltq
 * @date 2020-06-24
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "流程实例 ")
public class ProcessDefinitionAct{

    private String id;

    String category;

    String name;

    String key;

    String description;

    int version;

    String resourceName;

    String deploymentId;

    String diagramResourceName;

    boolean startFormKey;

    boolean graphicalNotation;

    boolean suspended;

    String tenantId;
}
