package com.cloud.settle.util;


import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.Constants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.system.feign.RemoteUserScopeService;

/**
 * 获取用户工厂、采购组权限
 */
public class DataScopeUtil {
    private static RedisUtils redis;

    static {
        DataScopeUtil.redis =  ApplicationContextUtil.getBean(RedisUtils.class);
    }

    /**
     * 用户数据权限
     */
    private final static String ACCESS_USERID_SCOPE_FACTORY = Constants.ACCESS_USERID_SCOPE_FACTORY;
    private final static String ACCESS_USERID_SCOPE_PURCHASE = Constants.ACCESS_USERID_SCOPE_PURCHASE;

    /**
     * 12小时后过期
     */
    private final static long EXPIRE = Constants.EXPIRE;


    /**
     * 获取用户采购组权限
     * @param userId
     * @return 逗号分隔字符串
     */
    public static String getUserPurchaseScopes(Long userId) {
        //先从redis里取值
        String scocpes = redis.get(Constants.ACCESS_USERID_SCOPE_PURCHASE + userId);
        if(StrUtil.isBlank(scocpes)){
            //如果redis没有值则从数据库中查询
            RemoteUserScopeService remoteUserScopeService = ApplicationContextUtil.getBean(RemoteUserScopeService.class);
            //采购组权限
            String purchaseScopes = remoteUserScopeService.selectDataScopeIdByUserIdAndType(userId, UserConstants.USER_SCOPE_TYPE_PURCHASE);
            redis.set(ACCESS_USERID_SCOPE_PURCHASE + userId, purchaseScopes, EXPIRE);
            return purchaseScopes;
        }
        return scocpes;
    }

    /**
     * 获取用户工厂权限
     * @param userId
     * @return 逗号分隔字符串
     */
    public static String getUserFactoryScopes(Long userId) {
        //先从redis里取值
        String scocpes = redis.get(Constants.ACCESS_USERID_SCOPE_FACTORY + userId);
        if(StrUtil.isBlank(scocpes)){
            //如果redis没有值则从数据库中查询
            RemoteUserScopeService remoteUserScopeService = ApplicationContextUtil.getBean(RemoteUserScopeService.class);
            String factoryScopes = remoteUserScopeService.selectDataScopeIdByUserIdAndType(userId, UserConstants.USER_SCOPE_TYPE_FACTORY);
            redis.set(ACCESS_USERID_SCOPE_FACTORY + userId, factoryScopes, EXPIRE);
            return factoryScopes;
        }
        return scocpes;
    }



}
