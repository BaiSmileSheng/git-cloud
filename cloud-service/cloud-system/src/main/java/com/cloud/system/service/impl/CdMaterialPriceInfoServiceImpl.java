package com.cloud.system.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.mapper.CdMaterialPriceInfoMapper;
import com.cloud.system.service.ICdMaterialPriceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * SAP成本价格 Service业务层处理
 *
 * @author cs
 * @date 2020-05-26
 */
@Service
public class CdMaterialPriceInfoServiceImpl extends BaseServiceImpl<CdMaterialPriceInfo> implements ICdMaterialPriceInfoService {
    @Autowired
    private CdMaterialPriceInfoMapper cdMaterialPriceInfoMapper;


    }
