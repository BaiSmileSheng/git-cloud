package com.cloud.activiti.constant;

public class ActProcessContants {

    /**
     * ZN认证服务相关流程
     */
    public static final String ACTIVITI_PRO_TITLE_ZN = "ZN认证审批流程";

    /**
     * ZN认证服务相关流程
     */
    public static final String ACTIVITI_PRO_TITLE_ADD = "T+2追加订单审批流程";
    /**
     * 3版本订单审批流程
     */
    public static final String ACTIVITI_PRO_TITLE_THREE_VERSION = "3版本订单审批流程";
    /**
     * 超期未关闭订单审批流程
     */
    public static final String ACTIVITI_PRO_TITLE_OVERDUE_NOT_CLOSE = "超期未关闭订单审批流程";

    /**
     * 超期库存订单审批流程
     */
    public static final String ACTIVITI_PRO_TITLE_OVERDUE_STOCK = "超期库存订单审批流程";
    /**
     * 原材料报废-有实物审批流程
     */
    public static final String ACTIVITI_BF_TITLE_RAW_SCRAP_OBJECT = "原材料报废-有实物审批流程";
    /**
     * 原材料报废-无实物审批流程
     */
    public static final String ACTIVITI_BF_TITLE_RAW_SCRAP_NO_OBJECT = "原材料报废-无实物审批流程";

    public static final String ACTIVITI_BF_TITLE_QUALITY_SCRAP_ZLGCS = "质量部报废申诉-质量经理审批流程";
    public static final String ACTIVITI_BF_TITLE_QUALITY_SCRAP_ZLBBZ = "质量部报废申诉-质量部长审批流程";
    public static final String ACTIVITI_BF_TITLE_QUALITY_SCRAP_ZLPTZ = "质量部报废申诉-质量平台长审批流程";


    /**
     * 审批流程Key
     * */
    //ZN认证审批流Key
    public static final String ACTIVITI_ZN_REVIEW = "productOrderZn";

    //T+2追加订单审批Key
    public static final String ACTIVITI_ADD_REVIEW = "productOrderAdd";

    //3版本订单审批Key
    public static final String ACTIVITI_THREE_VERSION_REVIEW = "productOrderThreeVersion";

    //超期未关闭订单审批Key
    public static final String ACTIVITI_OVERDUE_NOT_CLOSE_ORDER_REVIEW = "productOrderOverNotClose";

    //超期库存订单审批Key
    public static final String ACTIVITI_OVERDUE_STOCK_ORDER_REVIEW = "productOrderOverStock";

    //原材料报废有实物审批Key
    public static final String ACTIVITI_RAW_SCRAP_REVIEW = "rawScrap";
    //原材料报废无实物审批Key
    public static final String ACTIVITI_RAW_SCRAP_REVIEW_NO_OBJECT = "rawScrapNoObject";
    //质量部报废申诉审批流
    //质量经理审批流
    public static final String ACTIVITI_QUALITY_SCRAP_REVIEW_ZLGCS = "qualityScrapZLGCS";
    //质量部长审批流
    public static final String ACTIVITI_QUALITY_SCRAP_REVIEW_ZLBBZ = "qualityScrapZLBBZ";
    //质量平台长审批流
    public static final String ACTIVITI_QUALITY_SCRAP_REVIEW_ZLPTZ = "qualityScrapZLPTZ";

}
