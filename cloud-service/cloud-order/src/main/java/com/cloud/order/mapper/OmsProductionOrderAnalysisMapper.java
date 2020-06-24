package com.cloud.order.mapper;

import cn.hutool.core.lang.Dict;
import com.cloud.order.domain.entity.OmsProductionOrderAnalysis;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.vo.OmsProductionOrderAnalysisVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 待排产订单分析 Mapper接口
 *
 * @author ltq
 * @date 2020-06-15
 */
public interface OmsProductionOrderAnalysisMapper extends BaseMapper<OmsProductionOrderAnalysis>{
    int deleteAll();

    List<OmsProductionOrderAnalysisVo> selectListByGroup(OmsProductionOrderAnalysis omsProductionOrderAnalysis);

    List<OmsProductionOrderAnalysis> selectListByFactoryAndMaterial(@Param(value = "list") List<Dict> list);
}
