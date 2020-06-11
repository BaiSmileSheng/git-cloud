package com.cloud.order.enums;

/**
 * 排产订单状态枚举
 * @Author cs
 * @Date 2020-06-01
 */
public enum ProductionOrderStatusEnum {

    PRODUCTION_ORDER_STATUS_DPS("0","待评审"),
    PRODUCTION_ORDER_STATUS_FKZ("1","反馈中"),
    PRODUCTION_ORDER_STATUS_DTZ("2","待调整"),
    PRODUCTION_ORDER_STATUS_YTZ("3","已调整"),
    PRODUCTION_ORDER_STATUS_DCSAP("4","待传SAP"),
    PRODUCTION_ORDER_STATUS_YCSAP("5","已传SAP"),
    PRODUCTION_ORDER_STATUS_YGD("6","已关单"),


    ;
    private String code;
    private String msg;
    ProductionOrderStatusEnum(String code, String msg) {
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
        for (ProductionOrderStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (ProductionOrderStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
