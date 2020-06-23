package com.cloud.order.mapper;
import org.apache.ibatis.annotations.Param;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.OmsInternalOrderRes;

import java.util.List;

/**
 * 内单PR/PO原 Mapper接口
 *
 * @author ltq
 * @date 2020-06-05
 */
public interface OmsInternalOrderResMapper extends BaseMapper<OmsInternalOrderRes> {

    int deleteByMarker(@Param("marker")String marker);

    int batchInsertOrUpdate(List<OmsInternalOrderRes> omsInternalOrderResList);
}
