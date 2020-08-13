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

    PRODUCT_TYPE_1("1", "空调内板"),
    PRODUCT_TYPE_2("2", "空调外板"),
    PRODUCT_TYPE_3("3", "空调模块"),
    PRODUCT_TYPE_4("4", "空调显示"),
    PRODUCT_TYPE_5("5", "波轮显示"),
    PRODUCT_TYPE_6("6", "波轮电源驱动"),
    PRODUCT_TYPE_7("7", "滚筒显示"),
    PRODUCT_TYPE_8("8", "滚筒电源驱动"),
    PRODUCT_TYPE_9("9", "冰箱显示"),
    PRODUCT_TYPE_10("10", "冰箱主控"),
    PRODUCT_TYPE_11("11", "电热显示"),
    PRODUCT_TYPE_12("12", "电热主控"),
    PRODUCT_TYPE_13("13", "厨电显示"),
    PRODUCT_TYPE_14("14", "厨电主控"),
    PRODUCT_TYPE_15("15", "洗碗机"),
    PRODUCT_TYPE_16("16", "液晶模组"),
    PRODUCT_TYPE_17("17", "云单（购销业务）"),
    PRODUCT_TYPE_18("18", "海达维B2C"),
    PRODUCT_TYPE_19("19", "海达维照明模块"),
    PRODUCT_TYPE_20("20", "海达维杀菌模块"),
    PRODUCT_TYPE_21("21", "海达维购销物料"),
    PRODUCT_TYPE_22("22", "HMI"),
    PRODUCT_TYPE_23("23", "物联网模块"),
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
