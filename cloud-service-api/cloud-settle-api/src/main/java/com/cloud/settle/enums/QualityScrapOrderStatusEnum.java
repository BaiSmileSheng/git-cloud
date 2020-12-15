package com.cloud.settle.enums;

/**
 * 质量部报废申请单状态枚举
 *
 * @Author cs
 * @Date 2020-05-26
 */
public enum QualityScrapOrderStatusEnum {

    //报废申请单状态  0待提交、1供应商待确认，2供应商确认，3超时自动确认、4 质量经理审核、5 质量部长审核、6 质量平台长审核， 7 供应商待确认(申诉驳回)、 11待结算、12结算完成、、13已兑现、14部分兑现、15未兑现'
    ZLBBF_ORDER_STATUS_DTJ("0", "待提交"),
    ZLBBF_ORDER_STATUS_GYSDQR("1", "供应商待确认"),
    ZLBBF_ORDER_STATUS_GYSYQR("2", "供应商已确认"),
    ZLBBF_ORDER_STATUS_CSZDQR("3", "超时自动确认"),
    ZLBBF_ORDER_STATUS_ZLJLSH("4", "质量经理审核"),
    ZLBBF_ORDER_STATUS_ZLBZSH("5", "质量部长审核"),
    ZLBBF_ORDER_STATUS_ZLPTZSH("6", "质量平台长审核"),
    ZLBBF_ORDER_STATUS_SHBH("7", "供应商待确认(申诉驳回)"),
    ZLBBF_ORDER_STATUS_DJS("11", "待结算"),
    ZLBBF_ORDER_STATUS_YJS("12", "已结算"),
    ZLBBF_ORDER_STATUS_WXJS("13", "无需结算"),
    ZLBBF_ORDER_STATUS_BFDX("14", "部分兑现"),
    ZLBBF_ORDER_STATUS_WDX("15", "未兑现");

    private String code;
    private String msg;

    QualityScrapOrderStatusEnum(String code, String msg) {
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
        for (QualityScrapOrderStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (QualityScrapOrderStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
