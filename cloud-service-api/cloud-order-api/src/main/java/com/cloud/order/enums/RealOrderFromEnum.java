package com.cloud.order.enums;

/**
 * 真单表订单类型 枚举
 * @Author lihongxia
 * @Date 2020-06-17
 */
public enum RealOrderFromEnum {

    //订单类型
    ORDER_FROM_1("1","内单"),
    ORDER_FROM_2("2","外单"),

    ;
    private String code;
    private String msg;
    RealOrderFromEnum(String code, String msg) {
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
        for (RealOrderFromEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RealOrderFromEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
