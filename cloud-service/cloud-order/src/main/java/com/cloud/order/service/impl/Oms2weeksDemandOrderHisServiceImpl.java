package com.cloud.order.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderHis;
import com.cloud.order.mapper.Oms2weeksDemandOrderHisMapper;
import com.cloud.order.service.IOms2weeksDemandOrderHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * T+1-T+2周需求历史 Service业务层处理
 *
 * @author cs
 * @date 2020-06-12
 */
@Service
public class Oms2weeksDemandOrderHisServiceImpl extends BaseServiceImpl<Oms2weeksDemandOrderHis> implements IOms2weeksDemandOrderHisService {
    @Autowired
    private Oms2weeksDemandOrderHisMapper oms2weeksDemandOrderHisMapper;


    }
