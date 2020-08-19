package com.cloud.order.enums;

/**
 * 排产订单生成加工费结算单标记枚举
 * @Author lihongxia
 * @Date 2020-08-18
 */
public enum ProductionOrderSettleFlagEnum {

    //0:无需生成,1:待生成,2:已生成
    PRODUCTION_ORDER_SETTLE_FLAG_0("0","无需生成"),
    PRODUCTION_ORDER_SETTLE_FLAG_1("1","待生成"),
    PRODUCTION_ORDER_SETTLE_FLAG_2("2","已生成"),
    ;
    private String code;
    private String msg;
    ProductionOrderSettleFlagEnum(String code, String msg) {
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
        for (ProductionOrderSettleFlagEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (ProductionOrderSettleFlagEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
