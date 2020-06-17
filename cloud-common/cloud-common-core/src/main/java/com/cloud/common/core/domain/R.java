package com.cloud.common.core.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.common.constant.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class R extends Dict {
    //
    private static final long serialVersionUID = -8157613083634272196L;

    public R() {
        put("code", 0);
        put("msg", "success");
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R data(Object obj) {
        if (ObjectUtil.isEmpty(obj)) {
            R.error("数据为空！");
        }
        R r = new R();
        r.put("data", obj);
        return r;
    }

    public <T> T getData(Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T data = mapper.convertValue(super.get("data"), clazz);
        return data;
    }

    public <T> T getCollectData(TypeReference<T> typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        T data = mapper.convertValue(super.get("data"), typeReference);
        return data;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public boolean isSuccess() {
        return Constants.SUCCESS.equals(super.get("code").toString());
    }
}
