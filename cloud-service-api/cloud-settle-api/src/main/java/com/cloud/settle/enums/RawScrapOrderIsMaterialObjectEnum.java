package com.cloud.settle.enums;

/**
 * 报废申请单状态枚举
 *
 * @Author cs
 * @Date 2020-05-26
 */
public enum RawScrapOrderIsMaterialObjectEnum {

    //原材料报废有无实物
    YCLBF_ORDER_IS_MATERIAL_OBJECT_TRUE("0", "有"),
    YCLBF_ORDER_IS_MATERIAL_OBJECT_FALSE("1", "无");

    private String code;
    private String msg;

    RawScrapOrderIsMaterialObjectEnum(String code, String msg) {
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
        for (RawScrapOrderIsMaterialObjectEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RawScrapOrderIsMaterialObjectEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
