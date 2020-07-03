package com.cloud.system.enums;

/**
 * 是否ZN认证
 * @Author lihongxia
 * @Date 2020-07-02
 */
public enum ZnAttestationEnum {
    //0：否，1：是
    IS_ZN_ATTESTATION_0("0","否"),
    IS_ZN_ATTESTATION_1("1","是"),

    ;

    private String code;
    private String msg;

    ZnAttestationEnum(String code, String msg) {
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
        for (ZnAttestationEnum enums : ZnAttestationEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (ZnAttestationEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
