package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.vo.OmsProductionOrderVo;
import com.cloud.system.domain.entity.SysRole;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 排产订单 Service接口
 *
 * @author cs
 * @date 2020-05-29
 */
public interface IOmsProductionOrderService extends BaseService<OmsProductionOrder> {
    /**
     * Description:  排产订单导入
     * Param: [list, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    R importProductOrder(List<OmsProductionOrderVo> list, SysUser sysUser);

    /**
     * Description: 删除排产订单
     * Param: [ids]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    R deleteByIdString(String ids, SysUser sysUser);
    /**
     * Description:  排产订单修改
     * Param: [omsProductionOrder, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    R updateSave(OmsProductionOrder omsProductionOrder,SysUser sysUser);
    /**
     * Description:  确认下达
     * Param: [omsProductionOrder, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/23
     */
    R confirmRelease(OmsProductionOrder omsProductionOrder,SysUser sysUser);
    /**
     * Description:排产订单分页查询
     * Param: [omsProductionOrder, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/23
     */
    List<OmsProductionOrder> selectPageInfo(OmsProductionOrder omsProductionOrder,SysUser sysUser);
    /**
     * Description:  排产订单导出
     * Param: [omsProductionOrder, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderVo>
     * Author: ltq
     * Date: 2020/6/23
     */
    List<OmsProductionOrder> exportAll(OmsProductionOrder omsProductionOrder,SysUser sysUser);
}
