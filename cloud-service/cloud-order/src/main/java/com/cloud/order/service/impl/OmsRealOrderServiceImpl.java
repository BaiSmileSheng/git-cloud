package com.cloud.order.service.impl;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsRealOrderMapper;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 真单Service业务层处理
 *
 * @author ltq
 * @date 2020-06-15
 */
@Service
public class OmsRealOrderServiceImpl extends BaseServiceImpl<OmsRealOrder> implements IOmsRealOrderService {
    @Autowired
    private OmsRealOrderMapper omsRealOrderMapper;


    }
