package com.cloud.settle.enums;

/**
 * 报账单状态
 */
public enum QryPaysSoapStatusEnum {

    FLAG_SUCCESS("1","成功"),
    FLAG_FAIL("-1","单据不存在"),
    S_SUCCESS("S","支付成功"),
    A_SUCCESS("A","放弃支付"),
    R_SUCCESS("R","系统退回"),
    I_SUCCESS("I","支付中"),

    ;
    private String code;
    private String msg;
    QryPaysSoapStatusEnum(String code, String msg) {
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
