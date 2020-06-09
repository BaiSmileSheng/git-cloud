package com.cloud.order.service.impl;

    import com.cloud.order.domain.entity.OmsInternalOrderRes;
    import com.cloud.order.mapper.OmsInternalOrderResMapper;
    import com.cloud.order.service.IOmsInternalOrderResService;
    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.common.core.service.impl.BaseServiceImpl;
/**
 * 内单PR/PO原 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-05
 */
@Service
public class OmsInternalOrderResServiceImpl extends BaseServiceImpl<OmsInternalOrderRes> implements IOmsInternalOrderResService {
    @Autowired
    private OmsInternalOrderResMapper omsInternalOrderResMapper;


    }
