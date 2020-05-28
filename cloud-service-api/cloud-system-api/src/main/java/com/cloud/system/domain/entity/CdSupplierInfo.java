package com.cloud.system.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import java.util.Date;

/**
 * 供应商信息 对象 cd_supplier_info
 *
 * @author cs
 * @date 2020-05-28
 */
@ExcelIgnoreUnannotated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "供应商信息 ")
public class CdSupplierInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    /**
     * 昵称
     */
    @ExcelProperty(value = "昵称")
    @ApiModelProperty(value = "昵称")
    private String nick;

    /**
     * 账号类型 1:制造商，2：渠道商
     */
    @ExcelProperty(value = "账号类型 1:制造商，2：渠道商")
    @ApiModelProperty(value = "账号类型 1:制造商，2：渠道商")
    private String accountType;

    /**
     * 供应商代码
     */
    @ExcelProperty(value = "供应商代码")
    @ApiModelProperty(value = "供应商代码")
    private String supplierCode;

    /**
     * 审核状态
     */
    @ExcelProperty(value = "审核状态")
    @ApiModelProperty(value = "审核状态")
    private String approveStatus;

    /**
     * 入驻审核通过时间
     */
    @ExcelProperty(value = "入驻审核通过时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "入驻审核通过时间")
    private Date enterPassAt;

    /**
     * 用户来源
     */
    @ExcelProperty(value = "用户来源")
    @ApiModelProperty(value = "用户来源")
    private String origin;

    /**
     * 法人公司
     */
    @ExcelProperty(value = "法人公司")
    @ApiModelProperty(value = "法人公司")
    private String corporation;

    /**
     * 法人公司地址
     */
    @ExcelProperty(value = "法人公司地址")
    @ApiModelProperty(value = "法人公司地址")
    private String corpAddr;

    /**
     * 邮政编码
     */
    @ExcelProperty(value = "邮政编码")
    @ApiModelProperty(value = "邮政编码")
    private String zipcode;

    /**
     * 法人代表
     */
    @ExcelProperty(value = "法人代表")
    @ApiModelProperty(value = "法人代表")
    private String initAgent;

    /**
     * 公司固定资产
     */
    @ExcelProperty(value = "公司固定资产")
    @ApiModelProperty(value = "公司固定资产")
    private Long fixedAssets;

    /**
     * 公司固定资产币种
     */
    @ExcelProperty(value = "公司固定资产币种")
    @ApiModelProperty(value = "公司固定资产币种")
    private String faCoinType;

    /**
     * 注册资本
     */
    @ExcelProperty(value = "注册资本")
    @ApiModelProperty(value = "注册资本")
    private Long regCapital;

    /**
     * 注册资本币种
     */
    @ExcelProperty(value = "注册资本币种")
    @ApiModelProperty(value = "注册资本币种")
    private String rcCoinType;

    /**
     * 公司网站
     */
    @ExcelProperty(value = "公司网站")
    @ApiModelProperty(value = "公司网站")
    private String officialWebsite;

    /**
     * 规模人数 ,1:0-50,2:50-200,3:200-500,4:500-1000,5:1000以上
     */
    @ExcelProperty(value = "规模人数 ,1:0-50,2:50-200,3:200-500,4:500-1000,5:1000以上")
    @ApiModelProperty(value = "规模人数 ,1:0-50,2:50-200,3:200-500,4:500-1000,5:1000以上")
    private Integer personScale;

    /**
     * 成立时间
     */
    @ExcelProperty(value = "成立时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "成立时间")
    private Date foundAt;

    /**
     * 上市状态 0：没上市，1：已上市
     */
    @ExcelProperty(value = "上市状态 0：没上市，1：已上市")
    @ApiModelProperty(value = "上市状态 0：没上市，1：已上市")
    private String listedStatus;

    /**
     * 上市地区
     */
    @ExcelProperty(value = "上市地区")
    @ApiModelProperty(value = "上市地区")
    private String listedRegion;

    /**
     * 股票代码
     */
    @ExcelProperty(value = "股票代码")
    @ApiModelProperty(value = "股票代码")
    private String ticker;

    /**
     * 客户群(json)
     */
    @ExcelProperty(value = "客户群(json)")
    @ApiModelProperty(value = "客户群(json)")
    private String customers;

    /**
     * 工厂信息(json)
     */
    @ExcelProperty(value = "工厂信息(json)")
    @ApiModelProperty(value = "工厂信息(json)")
    private String factories;

    /**
     * 名字
     */
    @ExcelProperty(value = "名字")
    @ApiModelProperty(value = "名字")
    private String name;

    /**
     * 部门
     */
    @ExcelProperty(value = "部门")
    @ApiModelProperty(value = "部门")
    private String department;

    /**
     * 职务
     */
    @ExcelProperty(value = "职务")
    @ApiModelProperty(value = "职务")
    private String duty;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    @ApiModelProperty(value = "联系电话")
    private String mobile;

    /**
     * 办公电话
     */
    @ExcelProperty(value = "办公电话")
    @ApiModelProperty(value = "办公电话")
    private String officePhone;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 是否删除 0：有效，1：删除
     */
    private String delFlag;

}
