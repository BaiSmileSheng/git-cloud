package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.domain.entity.vo.OmsProductionOrderDetailVo;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 排产订单明细 Service接口
 *
 * @author ltq
 * @date 2020-06-19
 */
public interface IOmsProductionOrderDetailService extends BaseService<OmsProductionOrderDetail> {
    /**
     * Description:  根据orderCodes 查询
     * Param: [orderCodes]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    R selectListByOrderCodes(String orderCodes);
    /**
     * Description:根据排产订单号删除明细数据
     * Param: [productOrderCode]
     * return: int
     * Author: ltq
     * Date: 2020/6/24
     */
    int delectByProductOrderCode(String productOrderCode);
    /**
     * Description:  原材料评审-分页查询
     * Param: [omsProductionOrderDetail, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderDetailVo>
     * Author: ltq
     * Date: 2020/6/28
     */
    List<OmsProductionOrderDetailVo> listPageInfo(OmsProductionOrderDetail omsProductionOrderDetail, SysUser sysUser);

    /**
     * Description:  原材料评审导出
     * Param: [omsProductionOrderDetail, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    R exportList(OmsProductionOrderDetail omsProductionOrderDetail,SysUser sysUser);

    /**
     * Description: 反馈按钮，排产信息查询
     * Param: [omsProductionOrderDetail]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    R selectProductOrder(OmsProductionOrderDetail omsProductionOrderDetail);

    /**
     * Description: 原材料确认-列表查询
     * Param: [omsProductionOrderDetail, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    List<OmsProductionOrderDetail> commitListPageInfo(OmsProductionOrderDetail omsProductionOrderDetail,SysUser sysUser);
    /**
     * Description:  根据list查询
     * Param: [list]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrderDetail>
     * Author: ltq
     * Date: 2020/6/29
     */
    List<OmsProductionOrderDetail> selectListByList(List<OmsProductionOrderDetail> list);
    /**
     * Description: 原材料确认
     * Param: [omsProductionOrderDetail, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    R commitProductOrderDetail(List<OmsProductionOrderDetail> list,String  flag, SysUser sysUser);
    /**
     * Description:  根据排产订单号批量更新状态
     * Param: [list]
     * return: void
     * Author: ltq
     * Date: 2020/6/30
     */
    void updateBatchByProductOrderCode(List<OmsProductionOrderDetail> list);


}
