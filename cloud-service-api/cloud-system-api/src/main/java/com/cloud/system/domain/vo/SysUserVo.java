package com.cloud.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.common.core.domain.BaseEntity;
import com.cloud.common.easyexcel.converter.SexConverter;
import com.cloud.system.domain.entity.SysDept;
import com.cloud.system.domain.entity.SysRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户对象 sys_user
 *
 * @author cloud
 */
@ApiModel(value = "用户类")
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserVo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户序号")
    @ApiModelProperty(value = "用户序号")
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门父ID
     */
    private Long parentId;

    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 用户名称
     */
    @ExcelProperty(value = "用户名称")
    private String userName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别
     */
    @ExcelProperty(value = "性别",converter = SexConverter.class)
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐加密
     */
    private String salt;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 用户类型（1、海尔用户 2、外部用户）
     */
    private String userType;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登陆IP
     */
    private String loginIp;

    /**
     * 最后登陆时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /**
     * 部门对象
     */
    @ApiModelProperty(hidden = true)
    private SysDept dept;

    private List<SysRole> roles;

    /**
     * 角色Id组
     */
    private List<Long> roleIds;

    /**
     * 角色名称组
     */
    private List<String> roleKeys;

    /**
     * 岗位组
     */
    private Long[] postIds;

    private Set<String> buttons;

    /**
     * 外部用户 供应商V码
     */
    private String supplierCode;

    /**
     * 法人公司
     */
    private String corporation;

}
