package com.cloud.common.core.service;

import org.apache.ibatis.session.RowBounds;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 基类Service接口
 *
 * @param <T> 不能为空
 * @author cs
 * @Description 2020/4/30 9:54
 */
public interface BaseService<T> {

    /**
     * ----------------------------------------------------------- 新增接口
     */

    /**
     * 新增非空字段
     *
     * @param record
     */
    int insertSelective(T record);

    /**
     * 批量新增
     *
     * @param recordList
     */
    int insertList(List<? extends T> recordList);

    /**
     * 新增，采用数据库主键策略
     *
     * @param record
     */
    int insertUseGeneratedKeys(T record);


    /**
     * 根据主键字符串进行删除 逻辑删除
     * 类中只有存在一个带有@Id注解的字段
     *
     * @param ids 如 "1,2,3,4"
     */
    int deleteByIds(String ids);

    /**
     * 根据主键字符串进行删除 物理删除
     * 类中只有存在一个带有@Id注解的字段
     *
     * @param ids 如 "1,2,3,4"
     */
    int deleteByIdsWL(String ids);


    /**
     * ----------------------------------------------------------- 更新接口
     */

    /**
     * 根据主键更新非空字段
     *
     * @param record
     */
    int updateByPrimaryKeySelective(T record);



    /**
     * 根据Example条件更新非空字段
     *
     * @param record
     * @param example
     */
    int updateByExampleSelective(T record, Object example);

    /**
     * 批量更新
     *
     * @param recordList
     */
    int updateBatchByPrimaryKeySelective(List<? extends T> recordList);


    /**
     * ----------------------------------------------------------- 查询单个接口
     */
    /**
     * 根据主键查询
     *
     * @param key
     * @return
     */
    T selectByPrimaryKey(Object key);

    /**
     * 根据实体字段查询
     *
     * @param record
     * @return
     */
    T selectOne(T record);

    /**
     * 根据Example条件查询
     *
     * @param example
     * @return
     */
    T selectOneByExample(Object example);

    /**
     * ----------------------------------------------------------- 查询多个接口
     */
    /**
     * 根据实体非空字段查询
     *
     * @param record
     * @return
     */
    List<T> select(T record);



    /**
     * 根据Example条件查询
     *
     * @param example
     * @return
     */
    List<T> selectByExample(Example example);

    /**
     * 根据非空实体字段和边界查询
     * 能实现分页功能（不推荐使用）
     *
     * @param record
     * @param rowBounds
     * @return
     */
    List<T> selectByRowBounds(T record, RowBounds rowBounds);

    /**
     * 根据Example条件和边界查询
     *
     * @param example
     * @param rowBounds
     * @return
     */
    List<T> selectByExampleAndRowBounds(Object example, RowBounds rowBounds);

    /**
     * ----------------------------------------------------------- 计数接口
     */
    /**
     * 根据实体非空属性计数
     *
     * @param record
     * @return
     */
    int selectCount(T record);

    /**
     * 根据构造查询条件计数
     *
     * @param example
     * @return
     */
    int selectCountByExample(Object example);

    /**
     * 根据构造查询出一条数据
     * @param example
     * @return
     */
    T findByExampleOne(Example example);
}
