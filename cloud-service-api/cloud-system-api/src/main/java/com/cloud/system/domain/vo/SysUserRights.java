package com.cloud.system.domain.po;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description:  用户权限类
 * Param:
 * return:
 * Author: ltq
 * Date: 2020/6/19
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户权限类")
public class SysUserRights {

    private String id;

    private String userName;

    private String email;

    private List<String> productFactorys;

    private List<String> purchaseGroups;
}
