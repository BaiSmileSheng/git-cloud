package com.cloud.settle.enums;

/**
 * 延期索赔状态
 */
public enum DeplayStatusEnum {

    DELAYS_STATUS_1("1", "待供应商确认"),
    DELAYS_STATUS_4("4", "订单待审核"),
    DELAYS_STATUS_5("5", "小微主待审核"),
    DELAYS_STATUS_7("7", "待供应商确认(申诉驳回)"),
    DELAYS_STATUS_11("11", "待结算"),
    DELAYS_STATUS_12("12", "已结算"),
    DELAYS_STATUS_13("13", "已兑现"),
    DELAYS_STATUS_14("14", "部分兑现"),
    DELAYS_STATUS_15("15", "未兑现"),

    ;
    private String code;
    private String msg;

    DeplayStatusEnum(String code, String msg) {
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
    public static String getCodeByMsg(String msg) {
        for (DeplayStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }

}
