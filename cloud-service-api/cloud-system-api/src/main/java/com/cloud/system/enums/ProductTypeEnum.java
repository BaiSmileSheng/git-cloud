package com.cloud.system.enums;

/**
 * 产品类别
 *
 * @Author lihongxia
 * @Date 2020-07-02
 */
public enum ProductTypeEnum {
    //1.电热2.空调3.波轮4.商空5.外单6.冰箱7.滚筒8.冰箱 9.厨电10.辅料 11.半成品
    PRODUCT_TYPE_1("1", "电热"),
    PRODUCT_TYPE_2("2", "空调"),
    PRODUCT_TYPE_3("3", "波轮"),
    PRODUCT_TYPE_4("4", "商空"),
    PRODUCT_TYPE_5("5", "外单"),
    PRODUCT_TYPE_6("6", "冰箱"),
    PRODUCT_TYPE_7("7", "滚筒"),
    PRODUCT_TYPE_8("8", "冰箱"),
    PRODUCT_TYPE_9("9", "厨电"),
    PRODUCT_TYPE_10("10", "辅料"),
    PRODUCT_TYPE_11("11", "半成品"),

    ;
    private String code;
    private String msg;

    ProductTypeEnum(String code, String msg) {
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
        for (ProductTypeEnum enums : ProductTypeEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (ProductTypeEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
