package com.cloud.common.constant;

import java.math.BigDecimal;

public class ProductOrderConstants {
    /**
     * 状态
     * 状态：0：待评审，1：反馈中，2：待调整，3：已评审，4：待传SAP，5：传SAP中，6：已传SAP，7：传SAP异常，8：已关单',
     */
    public final static String STATUS_ZERO = "0";
    public final static String STATUS_ONE = "1";
    public final static String STATUS_TWO = "2";
    public final static String STATUS_THREE = "3";
    public final static String STATUS_FOUR = "4";
    public final static String STATUS_FIVE = "5";
    public final static String STATUS_SIX = "6";
    public final static String STATUS_SEVEN = "7";
    public final static String STATUS_EIGHT = "8";


    /**
     * 审核状态 0：无需审核，1：审核中，2：审核完成，3：驳回作废'
     * */
    public final static String AUDIT_STATUS_ZERO = "0";
    public final static String AUDIT_STATUS_ONE = "1";
    public final static String AUDIT_STATUS_TWO = "2";
    public final static String AUDIT_STATUS_THREE = "3";
    /**
     * 排产订单导入校验
     * */
    public static final String BOM_VERSION_THREE = "3";

    public static final BigDecimal BOM_VERSION_THREE_NUM = new BigDecimal("300");

    /**
     * 加工承揽方式
     *  0：半成品，1：成品，2：自制
     * */
    public static final String OUTSOURCE_TYPE_HALF_PRODUCT = "0";
    public static final String OUTSOURCE_TYPE_FINISHED_PRODUCT = "1";
    public static final String OUTSOURCE_TYPE_SELF_CONTROL = "2";

    /**
     * 排产订单明细状态
     * 0：未确认，1：已确认，2：反馈中,3反馈已确认'
     */
    public final static String DETAIL_STATUS_ZERO = "0";
    public final static String DETAIL_STATUS_ONE = "1";
    public final static String DETAIL_STATUS_TWO = "2";
    public final static String DETAIL_STATUS_THREE = "3";

    /**
     * 日期查询类型
     * 1：T-1交付日期，2：基本开始日期，3：基本结束日期'
     */
    public final static String DATE_TYPE_ONE = "1";
    public final static String DATE_TYPE_TWO = "2";
    public final static String DATE_TYPE_THREE = "3";

    /**
     * 订单分类
     * 1：正常，2：追加，3：储备，4：新品，5：返修'
     */
    public final static String ORDER_CLASS_ONE = "1";
    public final static String ORDER_CLASS_TWO = "2";
    public final static String ORDER_CLASS_THREE = "3";
    public final static String ORDER_CLASS_FOUR = "4";
    public final static String ORDER_CLASS_FIVE = "5";






}
