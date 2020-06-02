package com.cloud.system.service;


/**
 * 获取序列号 Service接口
 * @author Lihongxia
 * @date 2020-05-26
 */
public interface ISequeceService {

    /**
     * 获取序列号
     * @param name  序列名称
     * @param length 序列号长度
     * @return 序列号
     */
    String selectSeq(String name, int length);
}
