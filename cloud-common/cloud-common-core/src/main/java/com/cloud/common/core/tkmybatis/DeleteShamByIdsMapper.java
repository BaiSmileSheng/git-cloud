package com.cloud.common.core.tkmybatis;

import org.apache.ibatis.annotations.DeleteProvider;

/**
 * @auther cs
 * @date 2020/4/30 15:35
 * @description
 */
public interface DeleteShamByIdsMapper<T> {
    @DeleteProvider(
            type = IdsProviderDefined.class,
            method = "dynamicSQL"
    )
    int deleteShamByIds(String var1);
    //这里的抽象方法的名称必须和IdsProviderDefined 中的方法一致
}
