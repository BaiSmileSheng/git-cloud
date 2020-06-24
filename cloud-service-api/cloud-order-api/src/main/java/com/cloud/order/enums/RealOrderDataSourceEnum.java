package com.cloud.order.enums;

/**
 * 真单表 数据来源 枚举
 * @Author lihongxia
 * @Date 2020-06-17
 */
public enum RealOrderDataSourceEnum {

    //数据源
    DATA_SOURCE_0("0","接口接入"),
    DATA_SOURCE_1("1","人工导入"),
    ;
    private String code;
    private String msg;
    RealOrderDataSourceEnum(String code, String msg) {
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
        for (RealOrderDataSourceEnum bt : values()) {
            if (bt.code .equals(code) ) {
                return bt.getMsg();
            }
        }
        return code;
    }

    public static String getCodeByMsg(String msg) {
        for (RealOrderDataSourceEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
