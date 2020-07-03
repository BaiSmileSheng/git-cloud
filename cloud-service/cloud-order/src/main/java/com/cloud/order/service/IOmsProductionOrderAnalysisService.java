package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrderAnalysis;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.domain.entity.vo.OmsProductionOrderAnalysisVo;
import com.cloud.system.domain.entity.CdProductStock;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 待排产订单分析 Service接口
 *
 * @author ltq
 * @date 2020-06-15
 */
public interface IOmsProductionOrderAnalysisService extends BaseService<OmsProductionOrderAnalysis> {
    /**
     * Description:  待排产订单分析汇总
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/15
     */
    R saveAnalysisGather();
    /**
     * Description:  查询客户缺口量明细
     * Param: [omsRealOrder]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    R queryRealOrder(OmsRealOrder omsRealOrder);
    /**
     * Description:  分页查询
     * Param: [omsProductionOrderAnalysis]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderAnalysisVo>
     * Author: ltq
     * Date: 2020/6/18
     */
    List<OmsProductionOrderAnalysisVo> selectListPage(OmsProductionOrderAnalysis omsProductionOrderAnalysis);
    /**
     * Description:  查询成品库存信息
     * Param: [cdProductStock]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/30
     */
    R getProductStock(CdProductStock cdProductStock);
}
