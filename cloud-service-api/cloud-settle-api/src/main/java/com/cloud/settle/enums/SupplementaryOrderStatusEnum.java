package com.cloud.settle.enums;

/**
 * 物耗申请单状态枚举
 * @Author cs
 * @Date 2020-05-26
 */
public enum SupplementaryOrderStatusEnum {

    //物耗申请单状态
    WH_ORDER_STATUS_DTJ("0","待提交"),
    WH_ORDER_STATUS_JITSH("1","jit待审核"),
    WH_ORDER_STATUS_JITBH("2","jit驳回"),
    WH_ORDER_STATUS_XWZDSH("3","小微主待审核"),
    WH_ORDER_STATUS_XWZBH("5","小微主驳回"),
    WH_ORDER_STATUS_SAPSUCCESS("6","SAP成功"),
    WH_ORDER_STATUS_SAPFAIL("7","SAP创单失败"),
    WH_ORDER_STATUS_DJS("11","待结算"),
    WH_ORDER_STATUS_JSWC("12","结算完成"),
    WH_ORDER_STATUS_YDX("13","已兑现"),
    WH_ORDER_STATUS_BFDX("14","部分兑现"),
    WH_ORDER_STATUS_WDX("15","未兑现");

    private String code;
    private String msg;

    SupplementaryOrderStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
