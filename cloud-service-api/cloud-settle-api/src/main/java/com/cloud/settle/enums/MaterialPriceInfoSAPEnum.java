package com.cloud.settle.enums;

/**
 * 加工费/价格 同步SAP 枚举
 */
public enum MaterialPriceInfoSAPEnum {

    TYPE_S("S","成功"),
    TYPE_E("E","错误"),
    TYPE_W("W","警告"),
    TYPE_I("I","信息"),
    TYPE_A("A","中断"),

    ;
    private String code;
    private String msg;
    MaterialPriceInfoSAPEnum(String code, String msg) {
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
        for (DeplayStatusEnum enums : DeplayStatusEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
}
