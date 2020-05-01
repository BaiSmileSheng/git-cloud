package com.cloud.system.domain.entity;

import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cloud.common.core.domain.BaseEntity;

import lombok.*;

/**
 * 操作日志记录表 oper_log
 *
 * @author cloud
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ExcelIgnoreUnannotated
public class SysOperLog extends BaseEntity {
    //
    private static final long serialVersionUID = -5556121284445360558L;

    /**
     * 日志主键
     */
    @ExcelProperty(value = "操作序号",index = 0)
    private Long operId;

    /**
     * 操作模块
     */
    @ExcelProperty(value = "操作模块",index = 1)
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */

    private Integer businessType;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorType;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 请求url
     */
    private String operUrl;

    /**
     * 操作地址
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    private Date operTime;
}
