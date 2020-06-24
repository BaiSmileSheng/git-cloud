package com.cloud.common.core.tkmybatis;

import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * 批量操作接口
 *
 * @param <T> 不能为空
 * @author cs
 * @Description
 */
@RegisterMapper
public interface BatchMapper<T> extends UpdateBatchByPrimaryKeySelectiveMapper<T>,DeleteShamByIdsMapper<T> {
}
