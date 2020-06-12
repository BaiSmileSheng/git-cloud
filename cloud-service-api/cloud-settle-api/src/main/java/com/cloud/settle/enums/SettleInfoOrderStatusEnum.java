package com.cloud.settle.enums;

/**
 * 结算单订单状态枚举
 * @Author Lihongxia
 * @Date 2020-05-26
 */
public enum SettleInfoOrderStatusEnum {

    ORDER_STATUS_1("1","未关单"),
    ORDER_STATUS_2("2","已关单"),
    ORDER_STATUS_11("11","待结算"),
    ORDER_STATUS_12("12","结算完成"),


    ;
    private String code;
    private String msg;
    SettleInfoOrderStatusEnum(String code, String msg) {
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
        for (SettleInfoOrderStatusEnum enums : SettleInfoOrderStatusEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }

    public static String getCodeByMsg(String msg) {
        for (SettleInfoOrderStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
