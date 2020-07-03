package com.cloud.auth.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.Constants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.redis.annotation.RedisEvict;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserScopeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("accessTokenService")
public class AccessTokenService {
    @Autowired
    private RedisUtils redis;

    @Autowired
    private RemoteUserScopeService remoteUserScopeService;


    /**
     * 12小时后过期
     */
    private final static long EXPIRE = Constants.EXPIRE;

    private final static String ACCESS_TOKEN = Constants.ACCESS_TOKEN;

    private final static String ACCESS_USERID = Constants.ACCESS_USERID;

    /**
     * 用户数据权限
     */
    private final static String ACCESS_USERID_SCOPE_FACTORY = Constants.ACCESS_USERID_SCOPE_FACTORY;
    private final static String ACCESS_USERID_SCOPE_PURCHASE = Constants.ACCESS_USERID_SCOPE_PURCHASE;

    public SysUser queryByToken(String token) {
        return redis.get(ACCESS_TOKEN + token, SysUser.class);
    }

    @RedisEvict(key = "user_perms", fieldKey = "#sysUser.userId")
    public Map<String, Object> createToken(SysUser sysUser) {
        // 生成token
        String token = IdUtil.fastSimpleUUID();
        // 保存或更新用户token
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", sysUser.getUserId());
        map.put("token", token);
        map.put("expire", EXPIRE);
        // expireToken(userId);
        redis.set(ACCESS_TOKEN + token, sysUser, EXPIRE);
        redis.set(ACCESS_USERID + sysUser.getUserId(), token, EXPIRE);
        return map;
    }

    public void expireToken(long userId) {
        String token = redis.get(ACCESS_USERID + userId);
        if (StringUtils.isNotBlank(token)) {
            redis.delete(ACCESS_USERID + userId);
            redis.delete(ACCESS_USERID_SCOPE_FACTORY + userId);
            redis.delete(ACCESS_USERID_SCOPE_PURCHASE + userId);
            redis.delete(ACCESS_TOKEN + token);
        }
    }

    //更新用户数据权限到redis
    public void userScopeRedis(Long userId){
        //工厂权限
        String factoryScopes = remoteUserScopeService.selectDataScopeIdByUserIdAndType(userId, UserConstants.USER_SCOPE_TYPE_FACTORY);
        //采购组权限
        String purchaseScopes = remoteUserScopeService.selectDataScopeIdByUserIdAndType(userId, UserConstants.USER_SCOPE_TYPE_PURCHASE);
        if (StrUtil.isNotBlank(factoryScopes)) {
            redis.set(ACCESS_USERID_SCOPE_FACTORY + userId, factoryScopes, AccessTokenService.EXPIRE);
        }
        if (StrUtil.isNotBlank(purchaseScopes)) {
            redis.set(ACCESS_USERID_SCOPE_PURCHASE + userId, purchaseScopes, AccessTokenService.EXPIRE);
        }
    }
}
