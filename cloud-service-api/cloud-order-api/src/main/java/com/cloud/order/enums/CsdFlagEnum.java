package com.cloud.order.enums;

/**
 * 委外方式枚举
 * @Author cs
 * @Date 2020-06-01
 */
public enum CsdFlagEnum {

    OUT_SOURCE_TYPE_BWW("0","否"),
    OUT_SOURCE_TYPE_QWW("1","是"),
    ;
    private String code;
    private String msg;
    CsdFlagEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgByCode(String code) {
        for (CsdFlagEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (CsdFlagEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
