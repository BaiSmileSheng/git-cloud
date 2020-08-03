package com.cloud.settle.enums;

/**
 * 结算单订单状态枚举
 * @Author Lihongxia
 * @Date 2020-05-26
 */
public enum OutsourceWayEnum {

    OUTSOURCE_WAY_0("0","半成品"),
    OUTSOURCE_WAY_1("1","成品"),

    ;
    private String code;
    private String msg;
    OutsourceWayEnum(String code, String msg) {
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
        for (OutsourceWayEnum enums : OutsourceWayEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }

    public static String getCodeByMsg(String msg) {
        for (OutsourceWayEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
