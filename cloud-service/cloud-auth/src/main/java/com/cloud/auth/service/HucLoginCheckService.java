package com.cloud.auth.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloud.auth.form.HucProperties;
import com.cloud.common.constant.Constants;
import com.cloud.common.core.domain.R;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.okhttp.OkHttpClients;
import com.cloud.common.utils.okhttp.OkHttpParam;
import com.cloud.common.utils.okhttp.OkhttpResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther cs
 * @date 2020/5/7 17:26
 * @description HUC登录校验
 */
@Component
public class HucLoginCheckService {

    @Autowired
    private HucProperties hucProperties;

    @Autowired
    private RedisUtils redis;

    /**
     * 从HUC或者redis获取accessToken
     * @return
     * @throws Exception
     */
    public String getAccessToken() throws Exception {
        String accessToken = redis.get(Constants.HUC_ACCESS_TOKEN);
        if(StrUtil.isEmptyIfStr(accessToken)){
            OkHttpParam param = new OkHttpParam();
            param.setApiUrl(hucProperties.getApiUrl());
            param.setApiPath("/services/getToken");
            param.setMediaType(OkHttpParam.MEDIA_TYPE_NORAML_FORM);
            Map<String, String> map = new HashMap<>();
            map.put("appKey", hucProperties.getAppKey());
            map.put("secret", hucProperties.getSecret());
            OkhttpResult<String> result = OkHttpClients.post(param, map, String.class);
            JSONObject jsonObject = JSONUtil.parseObj(result.getResult());
            if(Boolean.valueOf(jsonObject.get("success").toString())){
                //返回错误为空，则获取正确的token 放入redis
                accessToken = jsonObject.get("result").toString();
                redis.set(Constants.HUC_ACCESS_TOKEN,accessToken,Constants.HUC_TOKEN_EXPIRE);
            }else{
                return null;
            }
        }
        return accessToken;
    }


    /**
     * HUC查询外部用户是否有效
     * @param userName
     * @param password
     * @param accessToken
     * @return
     * @throws Exception
     */
    public R checkHucUser(String userName,String password,String accessToken) throws Exception {
        OkHttpParam param = new OkHttpParam();
        param.setApiUrl(hucProperties.getApiUrl());
        param.setApiPath("/services/login");
        param.setMediaType(OkHttpParam.MEDIA_TYPE_NORAML_FORM);
        Map<String, String> map = new HashMap<>();
        map.put("token", accessToken);
        map.put("loginName", userName);
        map.put("pwd", password);
        OkhttpResult<String> result = OkHttpClients.post(param, map, String.class);
        if(result!=null&&result.getResult()!=null){
            JSONObject jsonObject = JSONUtil.parseObj(result.getResult());
            if(Boolean.valueOf(jsonObject.get("success").toString())){
                return R.ok();
            }else{
                return R.error(jsonObject.get("errorMessages").toString());
            }
        }else{
            return R.error("登录失败！");
        }
    }

}
