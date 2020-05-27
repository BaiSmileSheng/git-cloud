package com.cloud.settle.enums;

/**
 * 结算单时间类型枚举
 * @Author Lihongxia
 * @Date 2020-05-26
 */
public enum TimeTypeEnum {

    BASIC_BEGIN_TIME_TYPE("1","基本开始时间"),
    BASIC_END_TIME_TYPE("2","基本结束时间"),
    ACTUAL_END_TIME_TYPE("3","实际结束时间"),



    ;
    private String code;
    private String msg;
    TimeTypeEnum(String code, String msg) {
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
