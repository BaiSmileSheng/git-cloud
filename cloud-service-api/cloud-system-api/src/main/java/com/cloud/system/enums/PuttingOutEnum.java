package com.cloud.system.enums;

/**
 * 可否加工承揽
 *
 * @Author lihongxia
 * @Date 2020-07-02
 */
public enum PuttingOutEnum {
    //0：否，1：是
    IS_PUTTING_OUT_0("1", "否"),
    IS_PUTTING_OUT_1("1", "是"),
    ;
    private String code;
    private String msg;

    PuttingOutEnum(String code, String msg) {
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
        for (PuttingOutEnum enums : PuttingOutEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }

    public static String getCodeByMsg(String msg) {
        for (PuttingOutEnum bt : values()) {
            if (bt.msg.equals(msg)) {
                return bt.getCode();
            }
        }
        return msg;
    }
}
