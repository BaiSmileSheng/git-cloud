package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsProductionOrder;
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


    R importProductOrderTest(MultipartFile file);

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
     * 排产下达SAP导出
     * @param omsProductionOrder
     * @param sysUser
     * @return
     */
    R exportSAP(OmsProductionOrder omsProductionOrder,SysUser sysUser);
    /**
     * 下达SAP
     * @param omsProductionOrder
     * @return
     */
    R giveSAP(OmsProductionOrder omsProductionOrder);

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
    R mailPush(OmsProductionOrder omsProductionOrder);

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

    /**
     * 排产订单下达SAP删除排产订单
     */
    R deleteSAP(String id,SysUser sysUser);

    /**
     * Description:  定时任务校验排产订单审批流
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/10/19
     */
    R checkProductOrderAct(List<OmsProductionOrder> list);


    /**
     * 把delaysFlag=3、已关单、实际结束日期与基本开始日期小于等于7的数据更改把delaysFlag为0
     * @return
     */
	int updateDelaysFlag();

	List<OmsProductionOrder> selectByStatus(String status);


}
