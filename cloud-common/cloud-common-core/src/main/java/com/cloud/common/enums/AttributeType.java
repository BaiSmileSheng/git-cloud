package com.cloud.common.enums;

public enum AttributeType {
    ONE("1", "自制"), TWO("2", "工序"), THREE("3", "OEM");

    private final String code;
    private final String info;

    AttributeType(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}
