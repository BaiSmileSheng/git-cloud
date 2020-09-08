package com.cloud.system.enums;

/**
 * 是否取SAP成品库存
 * @Author lihongxia
 * @Date 2020-09-08
 */
public enum GetStockEnum {
    //0：否，1：是
    IS_GET_STOCK_0("0","否"),
    IS_GET_STOCK_1("1","是"),

    ;

    private String code;
    private String msg;

    GetStockEnum(String code, String msg) {
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
        for (GetStockEnum enums : GetStockEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (GetStockEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
