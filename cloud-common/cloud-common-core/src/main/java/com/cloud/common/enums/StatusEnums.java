package com.cloud.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 异常枚举类
 *
 * @author cs
 * @Description
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StatusEnums {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求语法错误"),
    UNAUTHORIZED(403, "未授权"),
    NOT_FOUND(404, "请求资源不存在"),
    USER_NOT_FOUND(500, "用户不存在"),
    PARAM_NOT_NULL(500, "参数不能为空"),
    SERVER_ERROR(500, "服务器异常"),
    CAPTCHA_ERROR(500, "验证码错误"),
    PASSWORD_ERROR(500, "密码错误"),
    SERVER_UNAVAILABLE(503, "服务器异常，请稍后重试"),
    METHOD_NOT_REALIZE(500,"方法暂未实现")
    ;

    private int code;

    private String msg;
}
