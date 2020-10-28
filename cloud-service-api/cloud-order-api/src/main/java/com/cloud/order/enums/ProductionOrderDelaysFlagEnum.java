package com.cloud.order.enums;

/**
 * 排产订单生成延期索赔标记枚举
 * @Author lihongxia
 * @Date 2020-08-18
 */
public enum ProductionOrderDelaysFlagEnum {

    //0:无需生成,1:待生成,2:已生成
    PRODUCTION_ORDER_DELAYS_FLAG_0("0","无需生成"),
    PRODUCTION_ORDER_DELAYS_FLAG_1("1","待生成"),
    PRODUCTION_ORDER_DELAYS_FLAG_2("2","已生成"),
    PRODUCTION_ORDER_DELAYS_FLAG_3("3","初始"),
    ;
    private String code;
    private String msg;
    ProductionOrderDelaysFlagEnum(String code, String msg) {
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
        for (ProductionOrderDelaysFlagEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (ProductionOrderDelaysFlagEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
