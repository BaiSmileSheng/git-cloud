package com.cloud.activiti.consts;

/**
 * biz_business 表名称存放值
 */
public class ActivitiTableNameConstants {
    /**
     * 报废申请表名称
     */
    public static final String ACTIVITI_TABLE_NAME_SCRAP = "sms_scrap_order";
    /**
     * 物耗申请表名称
     */
    public static final String ACTIVITI_TABLE_NAME_SUPPLEMENTARY = "sms_supplementary_order";
    /**
     * 质量索赔表名称
     */
    public static final String ACTIVITI_TABLE_NAME_QUALITY = "sms_quality_order";
    /**
     * 延期交付表名称
     */
    public static final String ACTIVITI_TABLE_NAME_DELAYS = "sms_delays_delivery";
    /**
     * 其他索赔表名称
     */
    public static final String ACTIVITI_TABLE_NAME_OTHER = "sms_claim_other";

    /**
     * 真单表名
     */
    public static final String ACTIVITI_TABLE_NAME_REAL_ORDER = "oms_real_order";

    /**
     *滚动计划需求操作表
     */
    public static final String ACTIVITI_TABLE_NAME_ORDER_GATHER_EDIT= "oms_demand_order_gather_edit";

    /**
     * T+1-T+2周需求导入表
     */
    public static final String ACTIVITI_TABLE_NAME_DEMAND_ORDER_EDIT = "oms2weeks_demand_order_edit";
}

