package com.cloud.common.constant;

/**
 * 通用常量信息
 *
 * @author cloud
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 自动去除表前缀
     */
    public static final String AUTO_REOMVE_PRE = "true";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "sortField";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "sortOrder";

    public static final String CURRENT_ID = "current_id";

    public static final String CURRENT_USERNAME = "current_username";

    public static final String TOKEN = "token";

    public static final String DEFAULT_CODE_KEY = "random_code_";

    public final static String ACCESS_TOKEN = "access_token_";

    public final static String ACCESS_USERID = "access_userid_";

    /**
     * uuc redis存放登录token key
     */
    public final static String UUC_ACCESS_TOKEN = "uuc_access_token";
    /**
     * huc redis存放登录token key
     */
    public final static String HUC_ACCESS_TOKEN = "huc_access_token";

    /**
     * 用户redis数据权限标记
     */
    public final static String ACCESS_USERID_SCOPE_FACTORY = "access_userid_scope_factory";//工厂
    public final static String ACCESS_USERID_SCOPE_PURCHASE = "access_userid_scope_purchase";//采购组

    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 12小时后过期
     */
    public final static long EXPIRE = 12 * 60 * 60;

    /**
     * HUC token 有效期  2小时
     */
    public final static long HUC_TOKEN_EXPIRE = 2 * 60 * 60;

}
