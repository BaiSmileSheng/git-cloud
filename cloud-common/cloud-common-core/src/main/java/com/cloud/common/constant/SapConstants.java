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

    public static final String SAP_RESULT_TYPE_ING = "W";

    public static final String SAP_RESULT_TYPE_REPEAT = "D";

    public static final String SAP_RESULT_TYPE_FAIL = "E";


    /**================卡奥斯SAP系统连接  start====================**/
    /**
     * 卡奥斯SAP系统连接
     * */
    public static final String ABAP_AS_SAP601 = "ABAP_AS_SAP601";
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
    /**
     * 获取加工费/原材料价格
     * */
    public static final String ZMM_INT_DDPS_01 = "ZMM_INT_DDPS_01";

    /**
     * 获取SAP成品库存信息
     */
    public static final String ZSD_INT_DDPS_02 = "ZSD_INT_DDPS_02";

    /**
     * 传输成品物料接口
     */
    public static final String ZSD_INT_DDPS_03= "ZSD_INT_DDPS_03";

    /**
     * 获取SAP销售价格
     */
    public static final String ZSD_INT_DDPS_01= "ZSD_INT_DDPS_01";

    /**
     * 实时取SAP成品库存
     */
    public static final String  ZSD_INT_DDPS_05 = "ZSD_INT_DDPS_05";

    /**
     * 实时取原材料库存接口
     */
    public static final String  ZPP_INT_DDPS_07 = "ZPP_INT_DDPS_07";

    //TODO ================卡奥斯SAP系统连接  end=====================

    /**
     * 报废、物耗发送SAP  261/Y61
     */
    public static final String ZESP_IM_001 = "ZESP_IM_001";
    /** ================卡奥斯SAP系统连接  end=====================**/


    /** ================SAP 800系统连接  start====================**/
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
    /** ================SAP 800系统连接  end====================**/
}
