package com.cloud.settle.enums;

/**
 * 其他索赔状态
 */
public enum ClaimOtherStatusEnum {

    //0待提交 1供应商待确认、3小微主待审核、7 供应商待确认(驳回) 11待结算、12 结算完成 、13已兑现、14部分兑现、15未兑现

    CLAIM_OTHER_STATUS_0("0","待提交"),
    CLAIM_OTHER_STATUS_1("1","供应商待确认"),
    CLAIM_OTHER_STATUS_3("3","小微主待审核"),
    CLAIM_OTHER_STATUS_7("7","供应商待确认(驳回)"),
    CLAIM_OTHER_STATUS_11("11","待结算"),
    CLAIM_OTHER_STATUS_12("12","结算完成"),
    CLAIM_OTHER_STATUS_13("13","已兑现"),
    CLAIM_OTHER_STATUS_14("14","部分兑现"),
    CLAIM_OTHER_STATUS_15("15","未兑现"),



    ;
    private String code;
    private String msg;
    ClaimOtherStatusEnum(String code, String msg) {
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
