package com.cloud.common.constant;
/**
 * @Description: sap 接口信息
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/5
 */
public class SapConstants {

    /**
     * SAP返回结果
     */
    public static final String SAP_RESULT_TYPE_SUCCESS = "S";

    public static final String SAP_RESULT_TYPE_FAIL = "E";


    //TODO ================卡奥斯SAP系统连接  start====================
    /**
     * 卡奥斯SAP系统连接
     * */
    public static final String ABAP_AS_SAP600 = "ABAP_AS_SAP600";
    /**
     * 获取uph数据接口函数
     * */
    public static final String ZMM_INT_DDPS_03 = "ZMM_INT_DDPS_03";
    /**
     * 获取原材料库存接口函数
     * */
    public static final String ZPP_INT_DDPS_01 = "ZPP_INT_DDPS_01";
    /**
     * 联动SAP创建生产订单接口函数
     * */
    public static final String ZPP_INT_DDPS_02 = "ZPP_INT_DDPS_02";
    /**
     * 获取SAP系统工厂线体关系数据接口函数
     * */
    public static final String ZPP_INT_DDPS_03 = "ZPP_INT_DDPS_03";
    /**
     * 传SAP计划需求
     * */
    public static final String ZPP_INT_DDPS_04 = "ZPP_INT_DDPS_04";
    /**
     * 获取SAP系统生产订单接口函数
     * */
    public static final String ZPP_INT_DDPS_05 = "ZPP_INT_DDPS_05";
    /**
     * 获取BOM清单数据
     * */
    public static final String ZPP_INT_DDPS_06 = "ZPP_INT_DDPS_06";
    //TODO ================卡奥斯SAP系统连接  end=====================


    //TODO ================SAP 800系统连接  start====================
    /**
     * SAP800 系统连接
     * */
    public static final String ABAP_AS_SAP800 = "ABAP_AS_SAP800";
    /**
     * 获取SAP800系统13周PR需求
     * */
    public static final String ZMM_PR_KAS = "ZMM_PR_KAS";
    /**
     * 获取SAP800系统PO真单
     * */
    public static final String ZMM_PO_KAS = "ZMM_PO_KAS";
    //TODO ================SAP 800系统连接  end====================
}
