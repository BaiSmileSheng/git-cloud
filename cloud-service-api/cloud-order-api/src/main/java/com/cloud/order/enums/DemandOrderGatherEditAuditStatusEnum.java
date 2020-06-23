package com.cloud.order.enums;

/**
 * 滚动计划需求操作审核状态枚举
 * @Author cs
 * @Date 2020-06-01
 */
public enum DemandOrderGatherEditAuditStatusEnum {
//审核状态 0：无需审核，1：审核中，2：审核完成
    DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_WXSH("0","无需审核"),
    DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHZ("1","审核中"),
    DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHWC("2","审核完成"),


    ;
    private String code;
    private String msg;
    DemandOrderGatherEditAuditStatusEnum(String code, String msg) {
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
        for (DemandOrderGatherEditAuditStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (DemandOrderGatherEditAuditStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
