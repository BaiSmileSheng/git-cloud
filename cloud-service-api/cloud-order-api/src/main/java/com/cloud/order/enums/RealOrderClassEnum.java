package com.cloud.order.enums;

/**
 * 真单表 订单种类 枚举
 * @Author lihongxia
 * @Date 2020-06-17
 */
public enum RealOrderClassEnum {

    //订单种类 1：正常，2：追加，3：储备，4：新品
    ORDER_CLASS_1("1","正常"),
    ORDER_CLASS_2("2","追加"),
    ORDER_CLASS_3("3","储备"),
    ORDER_CLASS_4("4","新品"),

    ;
    private String code;
    private String msg;
    RealOrderClassEnum(String code, String msg) {
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
        for (RealOrderClassEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RealOrderClassEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
