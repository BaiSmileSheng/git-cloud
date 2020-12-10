package com.cloud.settle.enums;

/**
 * 报废申请单状态枚举
 *
 * @Author cs
 * @Date 2020-05-26
 */
public enum RawScrapOrderIsCheckEnum {

    //原材料报废是否买单
    YCLBF_ORDER_IS_CHECK_TRUE("0", "是"),
    YCLBF_ORDER_IS_CHECK_FALSE("1", "否");

    private String code;
    private String msg;

    RawScrapOrderIsCheckEnum(String code, String msg) {
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
        for (RawScrapOrderIsCheckEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RawScrapOrderIsCheckEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
