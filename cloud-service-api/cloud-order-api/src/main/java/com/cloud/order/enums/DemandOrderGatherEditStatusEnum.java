package com.cloud.order.enums;

/**
 * 滚动计划需求操作状态枚举
 * @Author cs
 * @Date 2020-06-01
 */
public enum DemandOrderGatherEditStatusEnum {
//状态 0：初始，1：待传SAP，2：传SAP中，3：已传SAP，4：传SAP异常
    DEMAND_ORDER_GATHER_EDIT_STATUS_CS("0","初始"),
    DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP("1","待传SAP"),
    DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPZ("2","传SAP中"),
    DEMAND_ORDER_GATHER_EDIT_STATUS_YCSAP("3","已传SAP"),
    DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC("4","传SAP异常"),


    ;
    private String code;
    private String msg;
    DemandOrderGatherEditStatusEnum(String code, String msg) {
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
        for (DemandOrderGatherEditStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (DemandOrderGatherEditStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
