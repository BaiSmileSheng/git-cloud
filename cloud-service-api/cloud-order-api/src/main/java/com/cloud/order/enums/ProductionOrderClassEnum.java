package com.cloud.order.enums;

public enum  ProductionOrderClassEnum {

    ORDER_CLASS_ONE("1","正常"),
    ORDER_CLASS_TWO("2","追加"),
    ORDER_CLASS_THREE("3","储备"),
    ORDER_CLASS_FOUR("4","新品"),
    ORDER_CLASS_FIVE("5","返修"),
            ;
    private String code;
    private String msg;
    ProductionOrderClassEnum(String code, String msg) {
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
        for (ProductionOrderClassEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (ProductionOrderClassEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
