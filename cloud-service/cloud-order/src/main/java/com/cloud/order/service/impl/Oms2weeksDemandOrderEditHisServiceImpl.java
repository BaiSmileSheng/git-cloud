package com.cloud.order.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEditHis;
import com.cloud.order.mapper.Oms2weeksDemandOrderEditHisMapper;
import com.cloud.order.service.IOms2weeksDemandOrderEditHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * T+1-T+2周需求接入历史 Service业务层处理
 *
 * @author cs
 * @date 2020-06-22
 */
@Service
public class Oms2weeksDemandOrderEditHisServiceImpl extends BaseServiceImpl<Oms2weeksDemandOrderEditHis> implements IOms2weeksDemandOrderEditHisService {
    @Autowired
    private Oms2weeksDemandOrderEditHisMapper oms2weeksDemandOrderEditHisMapper;


    }
