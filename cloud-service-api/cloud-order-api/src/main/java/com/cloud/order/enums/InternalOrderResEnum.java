package com.cloud.order.enums;

/**
 * 内单PR/PO原表枚举
 * @Author lihongxia
 * @Date 2020-06-017
 */
public enum InternalOrderResEnum {

    //订单类型
    ORDER_TYPE_GN00("GN00","订单类型"),

    //PO/PR标记
    MARKER_PO("PO","PO标记"),
    MARKER_PR("PR","PR标记"),

    DELIVERY_FLAG_NO("0","未交货"),


    ;
    private String code;
    private String msg;
    InternalOrderResEnum(String code, String msg) {
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
        for (InternalOrderResEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (InternalOrderResEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
