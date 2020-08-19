package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.vo.OmsProductionOrderExportVo;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

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
    R importProductOrder(MultipartFile file, SysUser sysUser);

    /**
     * Description: 删除排产订单
     * Param: [ids]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    R deleteByIdString(OmsProductionOrder omsProductionOrder, SysUser sysUser);
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
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderExportVo>
     * Author: ltq
     * Date: 2020/6/23
     */
    List<OmsProductionOrder> exportAll(OmsProductionOrder omsProductionOrder,SysUser sysUser);
    /**
     * 下达SAP
     * @param ids
     * @return
     */
    R giveSAP(String ids);

    /**
     * SAP获取订单号
     */
    R timeSAPGetProductOrderCode();


    /**
     * 生成加工结算信息
     * @return
     */
    R insertSettleList();

    /**
     * 邮件推送
     * @return
     */
    R mailPush();

    /**
     * Description:  反馈信息处理-快捷修改查询
     * Param: [omsProductionOrder, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/28
     */
    List<OmsProductionOrder> queryProductOrder(OmsProductionOrder omsProductionOrder,SysUser sysUser);
    /**
     * Description: 根据工厂、专用号、基本开始日期查询
     * Param:
     * return:
     * Author: ltq
     * Date: 2020/6/28
     */
    List<OmsProductionOrder> selectByFactoryAndMaterialAndStartDate(List<OmsProductionOrder> list);
    /**
     * Description:  根据排产订单号查询
     * Param: [list]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/29
     */
    List<OmsProductionOrder> selectByOrderCode(List<String> list);
    /**
     * Description: 根据排产订单号批量更新
     * Param: [list]
     * return:
     * Author: ltq
     * Date: 2020/6/29
     */
    void updateByOrderCode(List<OmsProductionOrder> list);

    /**
     * 订单刷新
     * @param ids
     * @return
     */
    R orderRefresh(String ids);

    /**
     * 定时获取入库量
     * @return
     */
    R timeGetConfirmAmont();

}
