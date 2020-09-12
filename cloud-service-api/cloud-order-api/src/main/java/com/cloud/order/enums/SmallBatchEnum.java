package com.cloud.order.enums;

public enum SmallBatchEnum {

    SMALL_BATCH_TRUE("0","是"),
    SMALL_BATCH_FALSE("1","否");
    private String code;
    private String msg;
    SmallBatchEnum(String code, String msg) {
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
        for (SmallBatchEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (SmallBatchEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
