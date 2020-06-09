package com.cloud.system.service.impl;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdRawMaterialStockMapper;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.service.ICdRawMaterialStockService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 原材料库存 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-05
 */
@Service
public class CdRawMaterialStockServiceImpl extends BaseServiceImpl<CdRawMaterialStock> implements ICdRawMaterialStockService {
    @Autowired
    private CdRawMaterialStockMapper cdRawMaterialStockMapper;


    }
