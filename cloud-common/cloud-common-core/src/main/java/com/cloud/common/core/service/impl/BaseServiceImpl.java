package com.cloud.common.core.service.impl;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.common.core.service.BaseService;
import com.cloud.common.enums.StatusEnums;
import com.cloud.common.exception.SkeletonException;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @auther cs
 * @date 2020/4/30 9:59
 * @description
 */
public class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    public BaseMapper<T> baseMapper;

    public Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public int insertSelective(T record) {
        if (record == null) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.insertSelective(record);
    }

    @Override
    public int insertList(List<? extends T> recordList) {
        if (CollectionUtils.isEmpty(recordList)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.insertList((List<T>) recordList);
    }

    @Override
    public int insertUseGeneratedKeys(T record) {
        if (record == null) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.insertUseGeneratedKeys(record);
    }

    @Override
    public int deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        //逻辑删除
        return baseMapper.deleteShamByIds(ids);
    }

    @Override
    public int deleteByIdsWL(String ids) {
        if (StringUtils.isEmpty(ids)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        //物理删除
        return baseMapper.deleteByIds(ids);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        if (record == null) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByExampleSelective(T record, Object example) {
        if (record == null || ObjectUtils.isEmpty(example)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateBatchByPrimaryKeySelective(List<? extends T> recordList) {
        if (CollectionUtils.isEmpty(recordList)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.updateBatchByPrimaryKeySelective(recordList);
    }

    @Override
    public T selectByPrimaryKey(Object key) {
        if (ObjectUtils.isEmpty(key)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.selectByPrimaryKey(key);
    }

    @Override
    public T selectOne(T record) {
        if (record == null) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.selectOne(record);
    }

    @Override
    public T selectOneByExample(Object example) {
        if (ObjectUtils.isEmpty(example)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.selectOneByExample(example);
    }

    @Override
    public List<T> select(T record) {
        if (record == null) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.select(record);
    }

    @Override
    public List<T> selectByExample(Example example) {
        if (ObjectUtils.isEmpty(example)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        example.and().andEqualTo("delFlag", "0");
        return baseMapper.selectByExample(example);
    }

    @Override
    public List<T> selectByRowBounds(T record, RowBounds rowBounds) {
        if (record == null || rowBounds == null) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.selectByRowBounds(record, rowBounds);
    }

    @Override
    public List<T> selectByExampleAndRowBounds(Object example, RowBounds rowBounds) {
        if (ObjectUtils.isEmpty(example) || rowBounds == null) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    public int selectCount(T record) {
        if (ObjectUtils.isEmpty(record)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.selectCount(record);
    }

    @Override
    public int selectCountByExample(Object example) {
        if (ObjectUtils.isEmpty(example)) {
            throw new SkeletonException(StatusEnums.PARAM_NOT_NULL);
        }
        return baseMapper.selectCountByExample(example);
    }
}
