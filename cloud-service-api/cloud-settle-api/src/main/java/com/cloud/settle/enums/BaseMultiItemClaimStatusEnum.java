package com.cloud.settle.enums;

/**
 * 报账单状态
 */
public enum BaseMultiItemClaimStatusEnum {

    SUCCESS("S","成功"),
    FAIL("F","失败"),

    ;
    private String code;
    private String msg;
    BaseMultiItemClaimStatusEnum(String code, String msg) {
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
