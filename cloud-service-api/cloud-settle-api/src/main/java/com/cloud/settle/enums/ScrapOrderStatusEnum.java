package com.cloud.settle.enums;

/**
 * 报废申请单状态枚举
 *
 * @Author cs
 * @Date 2020-05-26
 */
public enum ScrapOrderStatusEnum {

    //报废申请单状态
    BF_ORDER_STATUS_DTJ("0", "待提交"),
    BF_ORDER_STATUS_YWKSH("1", "业务科待审核"),
    BF_ORDER_STATUS_YWKBH("2", "业务科驳回"),
    BF_ORDER_STATUS_PCYSH("3", "排产员待审核"),
    BF_ORDER_STATUS_PCYBH("4", "排产员驳回"),
    BF_ORDER_STATUS_ZLJLSH("5", "质量经理待审核-不买单"),
    BF_ORDER_STATUS_ZLJLBH("6", "质量经理驳回-不买单"),
    BF_ORDER_STATUS_DDJLSH("7", "订单经理待审核-不买单"),
    BF_ORDER_STATUS_DDJLBH("8", "订单经理驳回-不买单"),
    BF_ORDER_STATUS_YWKSHBMD("9", "业务科待审核-不买单"),
    BF_ORDER_STATUS_YWKBHBMD("10", "业务科驳回-不买单"),
    BF_ORDER_STATUS_DJS("11", "待结算"),
    BF_ORDER_STATUS_JSWC("12", "结算完成"),
    BF_ORDER_STATUS_YDX("13", "已兑现"),
    BF_ORDER_STATUS_BFDX("14", "部分兑现"),
    BF_ORDER_STATUS_WDX("15", "未兑现");
    //质量经理审核-订单经理审核-业务科审核   不买单审批
    //业务科审核-排产员审核   买单审批
    private String code;
    private String msg;

    ScrapOrderStatusEnum(String code, String msg) {
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
        for (ScrapOrderStatusEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (ScrapOrderStatusEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
