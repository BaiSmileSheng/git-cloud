package com.cloud.order.enums;

/**
 * 产品类别
 *
 * @Author lihongxia
 * @Date 2020-07-02
 */
public enum ProductTypeOrderEnum {
    //1.主控板,2.内板,3.电源驱动,4.显示,5.外板,6.模块,7.半成品,8.外单,9.外卖,10.海达维,11.冰箱,12.滚筒,13.波轮,14.商空,15.空调
    PRODUCT_TYPE_1("1", "主控板"),
    PRODUCT_TYPE_2("2", "内板"),
    PRODUCT_TYPE_3("3", "电源驱动"),
    PRODUCT_TYPE_4("4", "显示"),
    PRODUCT_TYPE_5("5", "外板"),
    PRODUCT_TYPE_6("6", "模块"),
    PRODUCT_TYPE_7("7", "半成品"),
    PRODUCT_TYPE_8("8", "外单"),
    PRODUCT_TYPE_9("9", "外卖"),
    PRODUCT_TYPE_10("10", "海达维"),
    PRODUCT_TYPE_11("11", "冰箱"),
    PRODUCT_TYPE_12("12", "滚筒"),
    PRODUCT_TYPE_13("13", "波轮"),
    PRODUCT_TYPE_14("14", "商空"),
    PRODUCT_TYPE_15("15", "空调"),

    ;
    private String code;
    private String msg;

    ProductTypeOrderEnum(String code, String msg) {
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
        for (ProductTypeOrderEnum enums : ProductTypeOrderEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (ProductTypeOrderEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
