package com.cloud.order.enums;

/**
 * 订单来源枚举
 * @Author cs
 * @Date 2020-06-01
 */
public enum OrderFromEnum {

    OUT_SOURCE_TYPE_BWW("1","内单"),
    OUT_SOURCE_TYPE_QWW("2","外单"),
    ;
    private String code;
    private String msg;
    OrderFromEnum(String code, String msg) {
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
        for (OrderFromEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (OrderFromEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
