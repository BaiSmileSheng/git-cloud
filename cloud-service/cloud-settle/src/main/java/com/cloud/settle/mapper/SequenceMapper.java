package com.cloud.settle.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 获取序列号
 *
 * @author Lihongxia
 * @date 2020-05-28
 */
public interface SequenceMapper {

    @Select("SELECT nextval(#{name})")
    int selectSeq(@Param("name") String name);
}
