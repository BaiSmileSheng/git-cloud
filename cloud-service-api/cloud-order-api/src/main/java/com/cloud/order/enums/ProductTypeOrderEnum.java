package com.cloud.order.enums;

/**
 * 产品类别
 *
 * @Author lihongxia
 * @Date 2020-07-02
 */
public enum ProductTypeOrderEnum {
    //内板空调,内板商空,主控板外单,显示冰箱,主控板冰箱,模块空调,外板商空,外板空调,主控板电热,显示电热,显示外单,显示空调,模块商空,主控板冰箱,主控板厨电
    //显示厨电,内板外单,电源驱动电热,显示商空,半成品冰箱,半成品电热,显示波轮,电源驱动波轮,电源驱动滚筒,显示滚筒,外单冰箱,外单波轮

    PRODUCT_TYPE_1("1", "内板空调"),
    PRODUCT_TYPE_2("2", "内板商空"),
    PRODUCT_TYPE_3("3", "主控板外单"),
    PRODUCT_TYPE_4("4", "显示冰箱"),
    PRODUCT_TYPE_5("5", "主控板冰箱"),
    PRODUCT_TYPE_6("6", "模块空调"),
    PRODUCT_TYPE_7("7", "外板商空"),
    PRODUCT_TYPE_8("8", "外板空调"),
    PRODUCT_TYPE_9("9", "主控板电热"),
    PRODUCT_TYPE_10("10", "显示电热"),
    PRODUCT_TYPE_11("11", "显示外单"),
    PRODUCT_TYPE_12("12", "显示空调"),
    PRODUCT_TYPE_13("13", "模块商空"),
    PRODUCT_TYPE_14("14", "主控板冰箱"),
    PRODUCT_TYPE_15("15", "主控板厨电"),
    PRODUCT_TYPE_16("16", "显示厨电"),
    PRODUCT_TYPE_17("17", "内板外单"),
    PRODUCT_TYPE_18("18", "电源驱动电热"),
    PRODUCT_TYPE_19("19", "显示商空"),
    PRODUCT_TYPE_20("20", "半成品冰箱"),
    PRODUCT_TYPE_21("21", "半成品电热"),
    PRODUCT_TYPE_22("22", "显示波轮"),
    PRODUCT_TYPE_23("23", "电源驱动波轮"),
    PRODUCT_TYPE_24("24", "电源驱动滚筒"),
    PRODUCT_TYPE_25("25", "显示滚筒"),
    PRODUCT_TYPE_26("26", "外单冰箱"),
    PRODUCT_TYPE_27("27", "外单波轮"),
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
