package com.cloud.order.enums;

public enum ProductionOrderDetailStatusEnum {

    ORDER_CLASS_ONE("0","未确认"),
    ORDER_CLASS_TWO("1","已确认"),
    ORDER_CLASS_THREE("2","反馈中")
            ;
    private String code;
    private String msg;
    ProductionOrderDetailStatusEnum(String code, String msg) {
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
        for (ProductionOrderDetailStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (ProductionOrderDetailStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
