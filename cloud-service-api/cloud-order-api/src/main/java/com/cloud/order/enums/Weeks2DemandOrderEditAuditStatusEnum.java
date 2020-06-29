package com.cloud.order.enums;

/**
 * T+2 T+3滚动计划需求操作审核状态枚举
 * @Author cs
 * @Date 2020-06-01
 */
public enum Weeks2DemandOrderEditAuditStatusEnum {
    //审核状态 0：无需审核，1：审核中，2：审核完成
    DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_WXSH("0","无需审核"),
    DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHZ("1","审核中"),
    DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHWC("2","审核完成"),
    DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHBH("3","审核驳回"),


    ;
    private String code;
    private String msg;
    Weeks2DemandOrderEditAuditStatusEnum(String code, String msg) {
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
        for (Weeks2DemandOrderEditAuditStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (Weeks2DemandOrderEditAuditStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
