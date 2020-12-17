package com.cloud.settle.enums;

/**
 * 报废申请单状态枚举
 *
 * @Author cs
 * @Date 2020-05-26
 */
public enum RawScrapOrderStatusEnum {

    //报废申请单状态
    YCLBF_ORDER_STATUS_DTJ("0", "待提交"),
    YCLBF_ORDER_STATUS_JITSH("1", "待JIT审核"),
    YCLBF_ORDER_STATUS_JITBH("2", "JIT驳回"),
    YCLBF_ORDER_STATUS_DDJLSH("3", "待订单经理审核"),
    YCLBF_ORDER_STATUS_DDJLBH("4", "订单经理驳回"),
    YCLBF_ORDER_STATUS_TRCCSH("5", "待投入产出审核"),
    YCLBF_ORDER_STATUS_TRCCBH("6", "投入产出驳回"),
    YCLBF_ORDER_STATUS_DJS("11", "待结算"),
    YCLBF_ORDER_STATUS_JSWC("12", "结算完成"),
    YCLBF_ORDER_STATUS_YDX("13", "已兑现"),
    YCLBF_ORDER_STATUS_BFDX("14", "部分兑现"),
    YCLBF_ORDER_STATUS_WDX("15", "未兑现");

    private String code;
    private String msg;

    RawScrapOrderStatusEnum(String code, String msg) {
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
        for (RawScrapOrderStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RawScrapOrderStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
