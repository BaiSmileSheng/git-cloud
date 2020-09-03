package com.cloud.system.enums;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.system.domain.entity.SysDictData;
import com.cloud.system.util.DynamicEnumUtils;

import java.util.List;

/**
 * 产品类别
 *
 * @Author lihongxia
 * @Date 2020-07-02
 */
public enum  ProductTypeEnum {

    ;
    private static RedisUtils redis;

    static {
        init();
    }

    private String code;
    private String msg;

    ProductTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static String getMsgByCode(String code) {
        for (ProductTypeEnum enums : ProductTypeEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (ProductTypeEnum bt : values()) {
            if (bt.msg .equals(msg) ) {
                return bt.getCode();
            }
        }
        return msg;
    }

    public static void init(){
        Console.log("-----------我执行了！！！！！！！！！！！---------");
        redis =  ApplicationContextUtil.getBean(RedisUtils.class);
        String rk = "dict:product_type";
        Object o=  redis.get(rk);
        List<SysDictData> list= JSON.parseObject((String) o, new TypeReference<List<SysDictData>>() {
        });
        int index = 0;
        for (SysDictData sysDictData : list) {
            DynamicEnumUtils.addEnum(ProductTypeEnum.class, StrUtil.format("PRODUCT_TYPE_{}",index), new Class[] {
                    java.lang.String.class, String.class }, new Object[] {
                    sysDictData.getDictValue(), sysDictData.getDictLabel() });
            index++;
        }
    }
}
