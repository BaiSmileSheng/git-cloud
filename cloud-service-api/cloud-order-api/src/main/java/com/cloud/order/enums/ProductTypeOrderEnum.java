package com.cloud.order.enums;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.order.domain.entity.vo.SysDictDataVo;
import com.cloud.order.util.DynamicEnumOrderUtils;

import java.util.List;

/**
 * 产品类别
 *
 * @Author lihongxia
 * @Date 2020-07-02
 */
public enum ProductTypeOrderEnum {

    ;
    private static RedisUtils redis;

    static {
        init();
    }

    private String code;
    private String msg;

    ProductTypeOrderEnum(String code, String msg) {
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
        for (ProductTypeOrderEnum enums : ProductTypeOrderEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getMsg();
            }
        }
        return "";
    }
    public static String getCodeByMsg(String msg) {
        for (ProductTypeOrderEnum bt : values()) {
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
        List<SysDictDataVo> list= JSON.parseObject((String) o, new TypeReference<List<SysDictDataVo>>() {
        });
        int index = 0;
        for (SysDictDataVo sysDictData : list) {
            DynamicEnumOrderUtils.addEnum(ProductTypeOrderEnum.class, StrUtil.format("PRODUCT_TYPE_{}",index), new Class[] {
                    java.lang.String.class, String.class }, new Object[] {
                    sysDictData.getDictValue(), sysDictData.getDictLabel() });
            index++;
        }
    }
}
