package com.cloud.order.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.OmsDemandOrderGather;
import com.cloud.order.mapper.OmsDemandOrderGatherMapper;
import com.cloud.order.service.IOmsDemandOrderGatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 滚动计划需求 Service业务层处理
 *
 * @author cs
 * @date 2020-06-12
 */
@Service
public class OmsDemandOrderGatherServiceImpl extends BaseServiceImpl<OmsDemandOrderGather> implements IOmsDemandOrderGatherService {
    @Autowired
    private OmsDemandOrderGatherMapper omsDemandOrderGatherMapper;


    }
