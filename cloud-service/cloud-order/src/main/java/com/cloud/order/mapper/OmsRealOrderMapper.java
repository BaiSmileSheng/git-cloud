package com.cloud.order.mapper;

import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.common.core.dao.BaseMapper;

import java.util.List;

/**
 * 真单Mapper接口
 *
 * @author ltq
 * @date 2020-06-15
 */
public interface OmsRealOrderMapper extends BaseMapper<OmsRealOrder>{

    List<OmsRealOrder> selectListByGroup(OmsRealOrder omsRealOrder);

}
