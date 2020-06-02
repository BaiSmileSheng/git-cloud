package com.cloud.system.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdBom;
import com.cloud.system.mapper.CdBomMapper;
import com.cloud.system.service.ICdBomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * bom清单数据 Service业务层处理
 *
 * @author cs
 * @date 2020-06-01
 */
@Service
public class CdBomServiceImpl extends BaseServiceImpl<CdBom> implements ICdBomService {
    @Autowired
    private CdBomMapper cdBomMapper;


}
