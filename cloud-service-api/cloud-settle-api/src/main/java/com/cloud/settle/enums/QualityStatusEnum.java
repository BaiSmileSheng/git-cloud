package com.cloud.settle.enums;

/**
 * 索赔单状态枚举
 * @Author Lihongxia
 * @Date 2020-05-26
 */
public enum QualityStatusEnum {

    QUALITY_STATUS_0("0","待提交"),
    QUALITY_STATUS_1("1","供应商待确认"),
    QUALITY_STATUS_2("2","供应商确认"),
    QUALITY_STATUS_3("3","超时自动确认"),
    QUALITY_STATUS_4("4","质量部待审核"),
    QUALITY_STATUS_5("5","小微主待审核"),
    QUALITY_STATUS_6("6","小微主审核通过"),
    QUALITY_STATUS_7("7","供应商待确认(申诉驳回)"),
    QUALITY_STATUS_11("11","待结算"),
    QUALITY_STATUS_12("12","结算完成"),

    ;
    private String code;
    private String msg;
    QualityStatusEnum(String code, String msg) {
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
        for (QualityStatusEnum enums : QualityStatusEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
}
