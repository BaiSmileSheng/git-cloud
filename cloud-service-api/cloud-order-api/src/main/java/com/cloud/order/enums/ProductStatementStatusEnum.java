package com.cloud.order.enums;

/**
 * T-1交付考核报表状态枚举
 * @Author lihongxia
 * @Date 2020-08-10
 */
public enum ProductStatementStatusEnum {

    PRODUCT_STATEMENT_STATUS_0("0","未关闭"),
    PRODUCT_STATEMENT_STATUS_1("1","已关闭"),
    ;
    private String code;
    private String msg;
    ProductStatementStatusEnum(String code, String msg) {
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
        for (ProductStatementStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (ProductStatementStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
