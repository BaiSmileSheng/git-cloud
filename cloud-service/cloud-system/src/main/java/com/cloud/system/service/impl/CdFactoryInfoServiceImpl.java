package com.cloud.system.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.mapper.CdFactoryInfoMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 工厂信息 Service业务层处理
 *
 * @author cs
 * @date 2020-06-03
 */
@Service
public class CdFactoryInfoServiceImpl extends BaseServiceImpl<CdFactoryInfo> implements ICdFactoryInfoService {
    @Autowired
    private CdFactoryInfoMapper cdFactoryInfoMapper;


    }
