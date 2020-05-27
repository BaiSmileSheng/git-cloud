package com.cloud.settle.enums;

/**
 * 结算单订单状态枚举
 * @Author Lihongxia
 * @Date 2020-05-26
 */
public enum SettleInfoOrderStatusEnum {

    ORDER_STATUS_0("0","已提交"),
    ORDER_STATUS_1("1","未关单"),
    ORDER_STATUS_2("2","已关单"),
    ORDER_STATUS_3("3","待结算"),
    ORDER_STATUS_4("4","已付款"),


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
}
