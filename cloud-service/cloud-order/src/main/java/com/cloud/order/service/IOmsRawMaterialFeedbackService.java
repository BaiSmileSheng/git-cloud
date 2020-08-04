package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.vo.OmsRawMaterialFeedbackVo;
import com.cloud.system.domain.entity.SysRole;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 原材料反馈信息 Service接口
 *
 * @author ltq
 * @date 2020-06-22
 */
public interface IOmsRawMaterialFeedbackService extends BaseService<OmsRawMaterialFeedback> {
    /**
     * Description:  反馈信息-分页查询
     * Param: [omsRawMaterialFeedbackVo]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsRawMaterialFeedbackVo>
     * Author: ltq
     * Date: 2020/6/28
     */
    List<OmsRawMaterialFeedback> listPage(OmsRawMaterialFeedback omsRawMaterialFeedback, SysUser sysUser);
    /**
     * Description:  反馈信息处理-通过/驳回
     * Param: [omsRawMaterialFeedback]
     * return: int
     * Author: ltq
     * Date: 2020/6/28
     */
    R approval(OmsRawMaterialFeedback omsRawMaterialFeedback,SysUser sysUser);

    /**
     * Description:  快捷修改排产订单量
     * Param: [list, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/28
     */
    R updateProductOrder(List<OmsProductionOrder> list,SysUser sysUser);
    /**
     * Description:  删除原材料反馈信息记录
     * Param: [ids, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/28
     */
    R deleteByIds(String ids,OmsRawMaterialFeedback omsRawMaterialFeedback,SysUser sysUser);
    /**
     * Description: 反馈信息新增
     * Param: [omsRawMaterialFeedbacks, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    R insertFeedback(List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks,SysUser sysUser);
    /**
     * Description:根据生产工厂、原材料物料、开始日期更新反馈信息状态为“未审核已确认”
     * Param: [list]
     * return: void
     * Author: ltq
     * Date: 2020/7/1
     */
    void updateBatchByList(List<OmsRawMaterialFeedback> list);


}
