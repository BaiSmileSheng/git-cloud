package com.cloud.settle.enums;

/**
 * 月度结算单状态
 * @Author cs
 * @Date 2020-05-26
 */
public enum MonthSettleStatusEnum {

    //11待结算、13内控确认、14小微主确认、15待付款、12结算完成
    YD_SETTLE_STATUS_DJS("11","待结算"),
    YD_SETTLE_STATUS_JSWC("12","结算完成"),
    YD_SETTLE_STATUS_NKQR("13","内控确认"),
    YD_SETTLE_STATUS_XWZQR("14","小微主确认"),
    YD_SETTLE_STATUS_DFK("15","待付款");


    private String code;
    private String msg;

    MonthSettleStatusEnum(String code, String msg) {
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
        for (MonthSettleStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (MonthSettleStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
