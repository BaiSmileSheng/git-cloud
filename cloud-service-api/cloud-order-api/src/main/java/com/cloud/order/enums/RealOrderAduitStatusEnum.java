package com.cloud.order.enums;

/**
 * 真单表审核状态 枚举
 * @Author lihongxia
 * @Date 2020-06-22
 */
public enum RealOrderAduitStatusEnum {

    //审核状态 0：无需审核，1：审核中，2：审核完成
    AUDIT_STATUS_WXSH("0","无需审核"),
    AUDIT_STATUS_SHZ("1","审核中"),
    AUDIT_STATUS_SHWC("2","审核完成"),
    AUDIT_STATUS_SHBO("3","审核驳回"),

    ;
    private String code;
    private String msg;
    RealOrderAduitStatusEnum(String code, String msg) {
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
        for (RealOrderAduitStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RealOrderAduitStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
