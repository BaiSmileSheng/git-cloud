package com.cloud.order.enums;

/**
 * 生命周期枚举
 * @Author cs
 * @Date 2020-06-16
 */
public enum LifeCycleOrderEnum {
//1、量产2、备件3、下市4、新品5、售后6、老品
    SMZQ_LC("1","量产"),
    SMZQ_BJ("2","纯备件"),
    SMZQ_XS("3","下市"),
    SMZQ_XP("4","新品"),

    ;

    private String code;
    private String msg;

    LifeCycleOrderEnum(String code, String msg) {
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
        for (LifeCycleOrderEnum enums : LifeCycleOrderEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (LifeCycleOrderEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
