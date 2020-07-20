package com.cloud.system.enums;

/**
 * 价格类型:0原材料价格,1加工费号加工费价格
 * @Author lihongxia
 * @Date 2020-07-16
 */
public enum PriceTypeEnum {
// 0原材料价格,1加工费号加工费价格
    PRICE_TYPE_0("0","原材料价格"),
    PRICE_TYPE_1("1","加工费号加工费价格"),

    ;
    private String code;
    private String msg;

    PriceTypeEnum(String code, String msg) {
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
        for (PriceTypeEnum enums : PriceTypeEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (PriceTypeEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
