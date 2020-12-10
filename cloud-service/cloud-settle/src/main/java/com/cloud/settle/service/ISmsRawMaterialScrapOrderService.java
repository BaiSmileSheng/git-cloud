package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 原材料报废申请Service接口
 *
 * @author ltq
 * @date 2020-12-07
 */
public interface ISmsRawMaterialScrapOrderService extends BaseService<SmsRawMaterialScrapOrder> {
   /**
    * 原材料报废新增
    * @author ltq
    * @date 2020-12-07
    */
   R insetRawScrap(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder, SysUser sysUser);

   /**
    * 审批通过传SAP261
    * @param smsRawMaterialScrapOrder
    * @return
    */
   R autidSuccessToSAP261(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder);
   /**
    * 更新修改原材料报废订单
    * @param smsRawMaterialScrapOrder
    * @return
    */
   R editRawScrap(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder,SysUser sysUser);
   /**
    * 删除原材料报废申请
    * @param ids
    * @return
    */
   R remove(String ids);
   /**
    * 定时任务更新价格
    * @param
    * @return
    */
   R updateRawScrapJob();

   List<SmsRawMaterialScrapOrder> selectByMonthAndStatus(String lastMonth,List<String> scrapStatus);
   /**
    * 提交
    * @param smsRawMaterialScrapOrder,sysUser
    * @return
    */
   R commit(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder,SysUser sysUser);
}
