package com.cloud.system.enums;

/**
 * 物料类型枚举
 * @Author cs
 * @Date 2020-06-16
 */
public enum MaterialTypeEnum {

    WLLX_HALB("HALB","成品"),
    WLLX_ROH("ROH","原材料");



    private String code;
    private String msg;

    MaterialTypeEnum(String code, String msg) {
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
