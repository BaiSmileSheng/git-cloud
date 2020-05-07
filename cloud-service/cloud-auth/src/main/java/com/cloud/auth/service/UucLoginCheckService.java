package com.cloud.auth.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloud.auth.form.UucProperties;
import com.cloud.common.constant.Constants;
import com.cloud.common.core.domain.R;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.IpUtils;
import com.cloud.common.utils.okhttp.OkHttpClients;
import com.cloud.common.utils.okhttp.OkHttpParam;
import com.cloud.common.utils.okhttp.OkhttpResult;
import com.cloud.common.utils.security.Aes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UucLoginCheckService {

    @Autowired
    private UucProperties uucProperties;

    @Autowired
    private RedisUtils redis;

    /**
     * 从UUC或者redis获取accessToken
     * @return
     * @throws Exception
     */
    public String getAccessToken() throws Exception {
        String accessToken = redis.get(Constants.UUC_ACCESS_TOKEN);
        if(StrUtil.isEmptyIfStr(accessToken)){
            OkHttpParam param = new OkHttpParam();
            param.setApiUrl(uucProperties.getApiUrl());
            param.setApiPath("/service/oauth/token");
            param.setMediaType(OkHttpParam.MEDIA_TYPE_NORAML_FORM);
            Map<String, String> map = new HashMap<>();
            map.put("client_id", uucProperties.getClientId());
            map.put("client_secret", uucProperties.getClientSecret());
            map.put("grant_type", "client_credentials");
            OkhttpResult<String> result = OkHttpClients.post(param, map, String.class);
            JSONObject jsonObject = JSONUtil.parseObj(result.getResult());
            if(StrUtil.isEmptyIfStr(jsonObject.get("error"))){
                //返回错误为空，则获取正确的token 放入redis
                redis.set(Constants.UUC_ACCESS_TOKEN,jsonObject.get("access_token"),Long.parseLong(jsonObject.get("expires_in").toString()));
                accessToken = jsonObject.get("access_token").toString();
            }else{
                return null;
            }
        }
        return accessToken;
    }

    /**
     * UUC查询用户是否有效
     * @param userName
     * @param password
     * @param accessToken
     * @return
     * @throws Exception
     */
    public R checkUucUser(String userName,String password,String accessToken) throws Exception {
        OkHttpParam param = new OkHttpParam();
        param.setApiUrl(uucProperties.getApiUrl());
        param.setApiPath("/service/idm/v2/login");
        param.setMediaType(OkHttpParam.MEDIA_TYPE_NORAML_FORM);
        Map<String, String> map = new HashMap<>();
        map.put("clientId", uucProperties.getClientId());
        map.put("clientSecret", uucProperties.getClientSecret());
        map.put("source", "1");
        map.put("username", userName);
        //登录密码加密
        String encryptPwd = Aes.encrypt(password);
        map.put("password", encryptPwd);
        //获取ip
        String ip = IpUtils.getHostIp();
        map.put("ip", ip);
        OkhttpResult<String> result = OkHttpClients.postWithAuthorize(param, map, String.class,accessToken);
        JSONObject jsonObject = JSONUtil.parseObj(result.getResult());
        if(StrUtil.isEmptyIfStr(jsonObject.get("error"))){
            return R.ok();
        }else{
            return R.error(jsonObject.get("error_description").toString());
        }
    }

}
