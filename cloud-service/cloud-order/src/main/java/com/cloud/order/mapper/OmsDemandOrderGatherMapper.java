package com.cloud.order.mapper;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.OmsDemandOrderGather;
/**
 * 滚动计划需求 Mapper接口
 *
 * @author cs
 * @date 2020-06-12
 */
public interface OmsDemandOrderGatherMapper extends BaseMapper<OmsDemandOrderGather>{

    int deleteAll();


}
