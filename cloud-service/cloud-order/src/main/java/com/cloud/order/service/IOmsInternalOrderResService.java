package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.OmsInternalOrderRes;

import java.util.List;

/**
 * 内单PR/PO原 Service接口
 *
 * @author ltq
 * @date 2020-06-05
 */
public interface IOmsInternalOrderResService extends BaseService<OmsInternalOrderRes>{

    /**
     * 将从SAP获取的PR信息插入
     * @param list
     * @return
     */
    R insert800PR(List<OmsInternalOrderRes> list);



    int deleteByMarker(String marker);

}
