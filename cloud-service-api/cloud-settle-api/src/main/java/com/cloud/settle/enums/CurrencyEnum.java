package com.cloud.settle.enums;

/**
 * 币种
 */
public enum CurrencyEnum {

    //美元 人民币

    CURRENCY_USD("USD","美元"),
    CURRENCY_CNY("CNY","人民币"),




    ;
    private String code;
    private String msg;
    CurrencyEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
