package com.cloud.order.enums;

/**
 * 真单表订单状态 枚举
 * @Author lihongxia
 * @Date 2020-06-17
 */
public enum RealOrderStatusEnum {

    //状态
    STATUS_0("0","初始"),
    STATUS_1("1","已调整"),

    ;
    private String code;
    private String msg;
    RealOrderStatusEnum(String code, String msg) {
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
        for (RealOrderStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RealOrderStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
