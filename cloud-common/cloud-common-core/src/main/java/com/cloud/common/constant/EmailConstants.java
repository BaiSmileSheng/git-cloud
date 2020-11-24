package com.cloud.common.constant;

public class EmailConstants {

    /**
     * 标题
     * */

    public static final String TITLE_THREE_VERSION_REVIEW = "3版本排产超配审批通知";

    public static final String TITLE_RAW_MATERIAL_REVIEW = "原材料评审邮件通知";

    public static final String TITLE_OVERDUE_NOT_CLOSE_ORDER_REVIEW = "超期未关闭订单审批通知";

    public static final String TITLE_ADD_ORDER_REVIEW = "T+2追加订单审批通知";

    public static final String TITLE_ZN_REVIEW = "ZN认证审批通知";

    public static final String TITLE_OVER_STOCK = "超期库存审批通知";

    public static final String TITLE_RAW_FEEDBACK = "原材料反馈信息通知";

    public static final String TITLE_RAW_MATERIAL = "原材料反馈信息处理-驳回通知";



    /**
     * 内容
     * */
    public static final String RAW_MATERIAL_REVIEW_CONTEXT = "，您好！\n 订单评审系统有新的原材料待评审，请及时评审！";

    public static final String THREE_VERSION_REVIEW_CONTEXT = "，您好！\n 订单评审系统有新的3版本超配订单待审批，请及时审批！";

    public static final String OVERDUE_NOT_CLOSE_ORDER_REVIEW_CONTEXT = "，您好！\n 订单评审系统有新的超期未关闭订单需审批，请及时审批！";

    public static final String ADD_ORDER_REVIEW_CONTEXT = "，您好！\n 订单评审系统有新的T+2追加订单待审批，请及时审批！";

    public static final String ZN_REVIEW_CONTEXT = "，您好！\n 订单评审系统有新的ZN认证订单待审批，请及时审批！";

    public static final String OVER_STOCK_CONTEXT = "，您好！\n 订单评审系统有新的超期库存订单待审批，请及时审批！";


    public static final String ORW_URL = "\n外网地址：http://orw-khaos.cosmoplat.com/\n" +
            "内网地址：http://orw.khaos.cosmoplat.com/";

    public static final String RAW_FEEDBACK_CONTEXT = "，您好！\n 订单评审系统有新的原材料反馈信息待处理，请及时处理！";

    public static final String RAW_MATERIAL_CONTEXT_FRONT = "<p>订单评审系统，原材料反馈信息处理模块排产员驳回列表如下：</p><html> " +
            "<body> " +
            "<table border= \"1\" cellspacing=\"0\" style=\"border-color: grey\">" +
            "<tr bgcolor=\"grey\">" +
            "<th style=\"width: 100px\">生产工厂</th>" +
            "<th style=\"width: 150px\">成品专用号</th>" +
            "<th style=\"width: 150px\">原材料号</th>" +
            "<th style=\"width: 150px\">排产员</th>" +
            "<th style=\"width: 150px\">反馈JIT</th>" +
            "<th style=\"width: 200px\">反馈原因</th>" +
            "</tr>";
    public static final String RAW_MATERIAL_CONTEXT_AFTER = "</table>" +
            "</body>" +
            "</html>";


}
