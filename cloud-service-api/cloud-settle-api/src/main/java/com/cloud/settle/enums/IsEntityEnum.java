package com.cloud.settle.enums;

/**
 * 有无实物枚举
 *
 * @Author cs
 * @Date 2020-05-26
 */
public enum IsEntityEnum {

    //有无实物
    IS_ENTITY_YES("0", "有"),
    IS_ENTITY_NO("1", "没有");


    private String code;
    private String msg;

    IsEntityEnum(String code, String msg) {
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
        for (IsEntityEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (IsEntityEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
