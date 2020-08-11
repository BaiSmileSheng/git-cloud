package com.cloud.order.mapper;

import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.domain.entity.vo.OmsProductionOrderDetailVo;
import com.cloud.order.domain.entity.vo.RawMaterialReviewDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 排产订单明细 Mapper接口
 *
 * @author ltq
 * @date 2020-06-19
 */
public interface OmsProductionOrderDetailMapper extends BaseMapper<OmsProductionOrderDetail>{
    /**
    * Description:  根据排产订单号字符串查询
    * Param:  orderCodes
    * return: List<OmsProductionOrderDetail>
    * Author: ltq
    * Date: 2020/7/1
    */
    List<OmsProductionOrderDetail> selectListByOrderCodes(@Param(value = "orderCodes") String orderCodes);
    /**
    * Description:  根据排产订单号删除
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/7/1
    */
    int deleteByProductOrderCode(@Param("productOrderCode")String productOrderCode);
    /**
    * Description: 分组查询14天数据
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/7/1
    */
    List<OmsProductionOrderDetailVo> selectListPageInfo(OmsProductionOrderDetail omsProductionOrderDetail);
    /**
    * Description: 根据生产工厂、原材料、采购组汇总到到天的数据
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/7/1
    */
    List<RawMaterialReviewDetailVo> selectRawMaterialDay(OmsProductionOrderDetail omsProductionOrderDetail);
    /**
    * Description: 原材料确认-列表查询
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/7/1
    */
    List<OmsProductionOrderDetail> selectCommitListPageInfo(OmsProductionOrderDetail omsProductionOrderDetail);
    /**
    * Description: 根据List查询
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/7/1
    */
    List<OmsProductionOrderDetail> selectListByList(@Param(value = "list") List<OmsRawMaterialFeedback> list);
    /**
    * Description: 根据排产订单号List查询
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/7/1
    */
    List<OmsProductionOrderDetail> selectByOrderCodeList(@Param(value = "list") List<String> list);
    /**
    * Description: 根据排产订单号、原材料批量更新
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/7/1
    */
    void updateBatchByProductOrderCode(@Param(value = "list") List<OmsProductionOrderDetail> list);
    /**
     * Description: 根据生产工厂、原材料、生产日期查询
     * Param:
     * return:
     * Author: ltq
     * Date: 2020/8/7
     */
    List<OmsProductionOrderDetail> selectByFactoryDateMaterialList(@Param(value = "list") List<OmsProductionOrderDetail> list);
}
