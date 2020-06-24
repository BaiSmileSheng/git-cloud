package com.cloud.order.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;
import com.cloud.order.mapper.Oms2weeksDemandOrderEditMapper;
import com.cloud.order.service.IOms2weeksDemandOrderEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * T+1-T+2周需求导入 Service业务层处理
 *
 * @author cs
 * @date 2020-06-22
 */
@Service
public class Oms2weeksDemandOrderEditServiceImpl extends BaseServiceImpl<Oms2weeksDemandOrderEdit> implements IOms2weeksDemandOrderEditService {
    @Autowired
    private Oms2weeksDemandOrderEditMapper oms2weeksDemandOrderEditMapper;


}
