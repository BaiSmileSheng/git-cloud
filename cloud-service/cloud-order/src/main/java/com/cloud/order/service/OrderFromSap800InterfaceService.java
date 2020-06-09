package com.cloud.order.service;

import com.cloud.common.core.domain.R;

import java.util.Date;

/**
 * @Description: order服务 - SAP800系统接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/5
 */
public interface OrderFromSap800InterfaceService {
     /**
      * @Description: 获取SAP800系统13周PR需求
      * @Param: [startDate, endDate]
      * @return: com.cloud.common.core.domain.R
      * @Author: ltq
      * @Date: 2020/6/5
      */
     R queryDemandPRFromSap800(Date startDate,Date endDate);
     /**
      * @Description: 获取SAP800系统PO真单
      * @Param: [startDate, endDate]
      * @return: com.cloud.common.core.domain.R
      * @Author: ltq
      * @Date: 2020/6/5
      */
     R queryDemandPOFromSap800(Date startDate,Date endDate);
}
