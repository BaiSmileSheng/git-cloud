package com.cloud.settle.enums;

/**
 * 是否买单枚举
 *
 * @Author cs
 * @Date 2020-05-26
 */
public enum IsPayEnum {

    //是否买单
    IS_PAY_YES("0", "买单"),
    IS_PAY_NO("1", "不买单");


    private String code;
    private String msg;

    IsPayEnum(String code, String msg) {
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
        for (IsPayEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (IsPayEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
