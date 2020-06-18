package com.cloud.order.enums;

/**
 * 真单表枚举
 * @Author lihongxia
 * @Date 2020-06-17
 */
public enum RealOrderEnum {

    //订单类型
    ORDER_FROM_1("1","内单"),
    ORDER_FROM_2("2","外单"),

    //订单种类 1：正常，2：追加，3：储备，4：新品
    ORDER_CLASS_1("1","正常"),
    ORDER_CLASS_2("2","追加"),
    ORDER_CLASS_3("3","储备"),
    ORDER_CLASS_4("4","新品"),

    //状态
    STATUS_1("0","初始"),
    STATUS_2("1","人工导入"),

    //数据源
    DATA_SOURCE_0("0","接口接入"),
    DATA_SOURCE_1("1","人工导入"),
    ;
    private String code;
    private String msg;
    RealOrderEnum(String code, String msg) {
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
        for (RealOrderEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RealOrderEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
