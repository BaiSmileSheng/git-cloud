package com.cloud.system.enums;

/**
 * 用户类型枚举
 * @Author Lihongxia
 * @Date 2020-05-26
 */
public enum UserTypeEnum {

    USER_TYPE_1("1","海尔用户"),
    USER_TYPE_2("2","外部用户"),


    ;
    private String code;
    private String msg;
    UserTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
