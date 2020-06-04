package com.cloud.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 获取序列号
 *
 * @author Lihongxia
 * @date 2020-05-28
 */
public interface SequenceMapper {

    /**
     * 获取序列号
     * @param name 序列名称
     * @return 序列下一值
     */
    @Select("SELECT nextval(#{name})")
    int selectSeq(@Param("name") String name);
}
