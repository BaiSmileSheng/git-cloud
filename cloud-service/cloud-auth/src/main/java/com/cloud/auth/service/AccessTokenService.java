package com.cloud.auth.service;

import java.util.HashMap;
import java.util.Map;

import com.cloud.system.feign.RemoteUserScopeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.common.constant.Constants;
import com.cloud.common.redis.annotation.RedisEvict;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.system.domain.entity.SysUser;

import cn.hutool.core.util.IdUtil;

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
    private final static String ACCESS_USERID_SCOPE = Constants.ACCESS_USERID_SCOPE;

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
            redis.delete(ACCESS_USERID_SCOPE + userId);
            redis.delete(ACCESS_TOKEN + token);
        }
    }

    //更新用户物料数据权限到redis
    public void userScopeRedis(Long userId){
        String scopes = remoteUserScopeService.selectDataScopeIdByUserId(userId);
        redis.set(Constants.ACCESS_USERID_SCOPE + userId, scopes, AccessTokenService.EXPIRE);
    }
}
