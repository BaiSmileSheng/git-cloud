package com.cloud.settle.enums;

/**
 * 更新索赔单标记
 * @Author lihongxia
 * @Date 2020-08-19
 */
public enum SettleUpdateFlagEnum {

    //更新索赔单标记 0:待更新,1:已更新(月度结算为已结算是更新已兑现的索赔单)
    UPDATE_FLAG_0("0","待更新"),
    UPDATE_FLAG_1("1","已更新"),


    ;


    private String code;
    private String msg;

    SettleUpdateFlagEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
