package com.cloud.system.enums;

/**
 * 索赔类型枚举
 * @Author cs
 * @Date 2020-06-04
 */
public enum SettleRatioEnum {

    SPLX_WH("WH","物耗"),
    SPLX_BF("BF","报废"),
    SPLX_ZL("ZL","质量"),
    SPLX_YQ("YQ","延期"),
    SPLX_QY("QY","其他");


    private String code;
    private String msg;

    SettleRatioEnum(String code, String msg) {
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
